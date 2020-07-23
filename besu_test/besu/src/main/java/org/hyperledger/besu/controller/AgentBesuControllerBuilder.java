package org.hyperledger.besu.controller;


import com.google.common.base.Splitter;
import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.config.GenesisConfigOptions;
import org.hyperledger.besu.config.AgentConfigOptions;
import org.hyperledger.besu.config.AgentFork;
import org.hyperledger.besu.consensus.common.BlockInterface;
import org.hyperledger.besu.consensus.common.EpochManager;
import org.hyperledger.besu.consensus.common.AgentForkingVoteTallyCache;
import org.hyperledger.besu.consensus.common.AgentValidatorOverrides;
import org.hyperledger.besu.consensus.common.VoteProposer;
import org.hyperledger.besu.consensus.common.VoteTallyCache;
import org.hyperledger.besu.consensus.common.VoteTallyUpdater;
import org.hyperledger.besu.consensus.ibft.AgentBlockTimer;
import org.hyperledger.besu.consensus.ibft.EthSynchronizerUpdater;
import org.hyperledger.besu.consensus.ibft.AgentEventMultiplexer;
import org.hyperledger.besu.consensus.ibft.AgentBlockInterface;
import org.hyperledger.besu.consensus.ibft.AgentContext;
import org.hyperledger.besu.consensus.ibft.AgentEventQueue;
import org.hyperledger.besu.consensus.ibft.AgentExecutors;
import org.hyperledger.besu.consensus.ibft.AgentGossip;
import org.hyperledger.besu.consensus.ibft.AgentProcessor;
import org.hyperledger.besu.consensus.ibft.AgentProtocolSchedule;
import org.hyperledger.besu.consensus.ibft.MessageTracker;
import org.hyperledger.besu.consensus.ibft.AgentRoundTimer;
import org.hyperledger.besu.consensus.ibft.UniqueMessageMulticaster;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentBlockCreatorFactory;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentMiningCoordinator;
import org.hyperledger.besu.consensus.ibft.blockcreation.ProposerSelector;
import org.hyperledger.besu.consensus.ibft.jsonrpc.IbftJsonRpcMethods;
import org.hyperledger.besu.consensus.ibft.network.ValidatorPeers;
import org.hyperledger.besu.consensus.ibft.payload.AgentMessageFactory;
import org.hyperledger.besu.consensus.ibft.protocol.AgentProtocolManager;
import org.hyperledger.besu.consensus.ibft.protocol.AgentSubProtocol;
import org.hyperledger.besu.consensus.ibft.statemachine.FutureMessageBuffer;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentBlockHeightManagerFactory;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentController;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentFinalState;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentRoundFactory;
import org.hyperledger.besu.consensus.ibft.validation.AgentMessageValidatorFactory;
import org.hyperledger.besu.crypto.AgentKeyGenerator;
import org.hyperledger.besu.crypto.AgentSignature;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.api.jsonrpc.methods.JsonRpcMethods;
import org.hyperledger.besu.ethereum.blockcreation.MiningCoordinator;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.chain.MinedBlockObserver;
import org.hyperledger.besu.ethereum.chain.MutableBlockchain;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.MiningParameters;
import org.hyperledger.besu.ethereum.core.Util;
import org.hyperledger.besu.ethereum.eth.EthProtocol;
import org.hyperledger.besu.ethereum.eth.manager.EthProtocolManager;
import org.hyperledger.besu.ethereum.eth.sync.state.SyncState;
import org.hyperledger.besu.ethereum.eth.transactions.TransactionPool;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;
import org.hyperledger.besu.ethereum.p2p.config.SubProtocolConfiguration;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.RawMessage;
import org.hyperledger.besu.ethereum.worldstate.WorldStateArchive;
import org.hyperledger.besu.util.Subscribers;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.Socket;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;


public class AgentBesuControllerBuilder extends BesuControllerBuilder {

    private static final Logger LOG = LogManager.getLogger();
    private AgentEventQueue agentEventQueue;
    private AgentConfigOptions agentConfig;
    private ValidatorPeers peers;
    private final BlockInterface blockInterface = new AgentBlockInterface();

    //1
    @Override
    protected void prepForBuild(){
        agentConfig = genesisConfig.getConfigOptions(genesisConfigOverrides).getAgentConfigOptions();
        agentEventQueue = new AgentEventQueue(agentConfig.getMessageQueueLimit());
    }

    //9 last
    @Override
    protected JsonRpcMethods createAdditionalJsonRpcMethodFactory(
            final ProtocolContext protocolContext) {
        return new IbftJsonRpcMethods(protocolContext);
    }

    //8
    @Override
    protected SubProtocolConfiguration createSubProtocolConfiguration(
            final EthProtocolManager ethProtocolManager) {

        return new SubProtocolConfiguration()
                .withSubProtocol(EthProtocol.get(), ethProtocolManager)
                .withSubProtocol(AgentSubProtocol.get(), new AgentProtocolManager(agentEventQueue, peers));

    }


    //6
    @Override
    protected MiningCoordinator createMiningCoordinator(
            final ProtocolSchedule protocolSchedule,
            final ProtocolContext protocolContext,
            final TransactionPool transactionPool,
            final MiningParameters miningParameters,
            final SyncState syncState,
            final EthProtocolManager ethProtocolManager) {

        //key Node Election
        //Todo: start to time check from here
        final VoteTallyCache voteTallyCache = protocolContext.getConsensusState(AgentContext.class).getVoteTallyCache();
        peers = new ValidatorPeers(voteTallyCache);
        List<String> addressList = peers.getListValidators();

        final MutableBlockchain blockchain = protocolContext.getBlockchain();

        String previousBlock = String.valueOf(blockchain.getChainHeadHash());
        Random rand = new Random();
        int seed = previousBlock.hashCode();
        rand.setSeed(seed);
        int randomResult = rand.nextInt(addressList.size());
        String keyNode = addressList.get(randomResult);

        String nodeAddress = String.valueOf(Util.publicKeyToAddress(nodeKey.getPublicKey()));

        boolean keyNodeStart = keyNodeOn(keyNode, nodeAddress, addressList, randomResult, previousBlock);



        final ProposerSelector proposerSelector =
                new ProposerSelector(blockchain, blockInterface, true, voteTallyCache);

        final UniqueMessageMulticaster uniqueMessageMulticaster =
                new UniqueMessageMulticaster(peers, agentConfig.getGossipedHistoryLimit());

        final AgentGossip gossiper = new AgentGossip(uniqueMessageMulticaster);

        final MessageTracker duplicateMessageTracker =
                new MessageTracker(agentConfig.getDuplicateMessageLimit());

        String agentOnMessage = "agentOn";
        MessageData md = create(agentOnMessage);

        if (keyNodeStart){
            System.out.println("=======================================================AGENT ON!!!!!!!!!!!!");
            peers.send(md);
            duplicateMessageTracker.addSeenMessage(md);
        }

        System.out.println("======================================================="+keyNodeStart);


        final AgentExecutors agentExecutors = AgentExecutors.create(metricsSystem);
        final AgentBlockCreatorFactory blockCreatorFactory =
                new AgentBlockCreatorFactory(
                        gasLimitCalculator,
                        transactionPool.getAgentPendingTransactions(),
                        protocolContext,
                        protocolSchedule,
                        miningParameters,
                        Util.publicKeyToAddress(nodeKey.getPublicKey()));


        // NOTE: peers should not be used for accessing the network as it does not enforce the
        // "only send once" filter applied by the UniqueMessageMulticaster.


        final AgentFinalState finalState =
                new AgentFinalState(
                        voteTallyCache,
                        nodeKey,
                        Util.publicKeyToAddress(nodeKey.getPublicKey()),
                        proposerSelector,
                        uniqueMessageMulticaster,
                        new AgentRoundTimer(agentEventQueue, agentConfig.getRequestTimeoutSeconds(), agentExecutors),
                        new AgentBlockTimer(
                                agentEventQueue, agentConfig.getBlockPeriodSeconds(), agentExecutors, clock),
                        blockCreatorFactory,
                        new AgentMessageFactory(nodeKey),
                        clock);
        final AgentMessageValidatorFactory messageValidatorFactory =
                new AgentMessageValidatorFactory(proposerSelector, protocolSchedule, protocolContext);
        final Subscribers<MinedBlockObserver> minedBlockObservers = Subscribers.create();
        minedBlockObservers.subscribe(ethProtocolManager);
        final FutureMessageBuffer futureMessageBuffer =
                new FutureMessageBuffer(
                        agentConfig.getFutureMessagesMaxDistance(),
                        agentConfig.getFutureMessagesLimit(),
                        blockchain.getChainHeadBlockNumber());

        final AgentController agentController =
                new AgentController(
                        blockchain,
                        finalState,
                        new AgentBlockHeightManagerFactory(
                                finalState,
                                new AgentRoundFactory(
                                        finalState,
                                        protocolContext,
                                        protocolSchedule,
                                        minedBlockObservers,
                                        messageValidatorFactory),
                                messageValidatorFactory),
                        gossiper,
                        duplicateMessageTracker,
                        futureMessageBuffer,
                        new EthSynchronizerUpdater(ethProtocolManager.ethContext().getEthPeers()));


        final AgentEventMultiplexer eventMultiplexer = new AgentEventMultiplexer(agentController);
        final AgentProcessor agentProcessor = new AgentProcessor(agentEventQueue, eventMultiplexer);
        final MiningCoordinator agentMiningCoordinator =
                new AgentMiningCoordinator(
                        agentExecutors,
                        agentController,
                        agentProcessor,
                        blockCreatorFactory,
                        blockchain,
                        agentEventQueue);
        agentMiningCoordinator.enable();

        return agentMiningCoordinator;
    }

    //7
    @Override
    protected PluginServiceFactory createAdditionalPluginServices(final Blockchain blockchain) {

        return new AgentQueryPluginServiceFactory(blockchain, nodeKey);
    }

    //2
    @Override
    protected ProtocolSchedule createProtocolSchedule() {

        return AgentProtocolSchedule.create(
                genesisConfig.getConfigOptions(genesisConfigOverrides),
                privacyParameters,
                isRevertReasonEnabled);
    }

    //5
    @Override
    protected void validateContext(final ProtocolContext context) {
        final BlockHeader genesisBlockHeader = context.getBlockchain().getGenesisBlock().getHeader();

        if (blockInterface.validatorsInBlock(genesisBlockHeader).isEmpty()) {
            LOG.warn("Genesis block contains no signers - chain will not progress.");
        }
    }

    //4
    @Override
    protected AgentContext createConsensusContext(
            final Blockchain blockchain, final WorldStateArchive worldStateArchive) {
        final GenesisConfigOptions configOptions =
                genesisConfig.getConfigOptions(genesisConfigOverrides);
        final AgentConfigOptions agentConfig = configOptions.getAgentConfigOptions();
        final EpochManager epochManager = new EpochManager(agentConfig.getEpochLength());
        final Map<Long, List<Address>> agentValidatorForkMap =
                convertAgentForks(configOptions.getTransitions().getAgentForks());

        return new AgentContext(
                new AgentForkingVoteTallyCache(
                        blockchain,
                        new VoteTallyUpdater(epochManager, new AgentBlockInterface()),
                        epochManager,
                        new AgentBlockInterface(),
                        new AgentValidatorOverrides(agentValidatorForkMap)),
                new VoteProposer(),
                epochManager,
                blockInterface);
    }

    //3
    private Map<Long, List<Address>> convertAgentForks(final List<AgentFork> agentForks) {
        final Map<Long, List<Address>> result = new HashMap<>();

        for (final AgentFork fork : agentForks) {
            fork.getValidators()
                    .map(
                            validators ->
                                    result.put(
                                            fork.getForkBlock(),
                                            validators.stream()
                                                    .map(Address::fromHexString)
                                                    .collect(Collectors.toList())));
        }

        return result;
    }

    private boolean keyNodeOn
            (final String keyNode,
             final String nodeAddress,
             final List<String>addressList,
             final Integer randomResult,
             final String previousBlock ){

        boolean sigVerify = false;
        if (keyNode.equals(nodeAddress)){

        try {
            AgentKeyGenerator key = new AgentKeyGenerator();
            AgentSignature agentSignature = new AgentSignature();
            String AgentConfig = genesisConfig.AgentInfo();

            List<String> AgentInfo = Splitter.on(":").splitToList(AgentConfig);

            String ip = AgentInfo.get(0);
            Integer port = Integer.valueOf(AgentInfo.get(1));
            Socket sc = new Socket(ip,port);
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            DataInputStream in = new DataInputStream(sc.getInputStream());


                KeyPair getKey = key.genKey();
                PrivateKey agentPrivateKey = getKey.getPrivate();
                PublicKey agentPublicKey = getKey.getPublic();

                String strAgentPublicKey = key.pubKeyToString(agentPublicKey);
                String randomSize = String.valueOf(addressList.size());
                String strRandomResult = String.valueOf(randomResult);

                String message = previousBlock+":"+randomSize+":"+strRandomResult;

                String agentOnSignature = agentSignature.genSig(agentPrivateKey, message);

                String agentOnMessage = strAgentPublicKey+":"+message+":"+agentOnSignature;

                out.writeUTF(agentOnMessage);

                String agentMessage = in.readUTF();

                List<String> parAgentMessage = Splitter.on(":").splitToList(agentMessage);

                String agentStrMessage = parAgentMessage.get(0);
                String agentStrPubKey = parAgentMessage.get(1);
                String agentStrSignature = parAgentMessage.get(2);

                PublicKey agentPubKey = key.stringToPublicKey(agentStrPubKey);
                sigVerify = agentSignature.verify(agentStrMessage, agentStrSignature, agentPubKey);

                return sigVerify;
            }catch (Exception e) {
            System.out.println(e);
        }

        }

        return sigVerify;

    }

    private MessageData create(final String message){
        String hexString = String.format("%040x", new BigInteger(1, message.getBytes(UTF_8)));

        RawMessage rm = new RawMessage(8, Bytes.fromHexString(hexString));


        return rm;
    }

}
