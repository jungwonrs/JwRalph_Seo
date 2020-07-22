package org.hyperledger.besu.controller;


import com.google.common.base.Splitter;
import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.config.AgentConfigOptions;
import org.hyperledger.besu.config.GenesisConfigOptions;
import org.hyperledger.besu.config.IbftFork;
import org.hyperledger.besu.consensus.common.BlockInterface;
import org.hyperledger.besu.consensus.common.EpochManager;
import org.hyperledger.besu.consensus.common.ForkingVoteTallyCache;
import org.hyperledger.besu.consensus.common.IbftValidatorOverrides;
import org.hyperledger.besu.consensus.common.VoteProposer;
import org.hyperledger.besu.consensus.common.VoteTallyCache;
import org.hyperledger.besu.consensus.common.VoteTallyUpdater;
import org.hyperledger.besu.consensus.ibft.BlockTimer;
import org.hyperledger.besu.consensus.ibft.EthSynchronizerUpdater;
import org.hyperledger.besu.consensus.ibft.EventMultiplexer;
import org.hyperledger.besu.consensus.ibft.IbftBlockInterface;
import org.hyperledger.besu.consensus.ibft.IbftContext;
import org.hyperledger.besu.consensus.ibft.AgentEventQueue;
import org.hyperledger.besu.consensus.ibft.IbftExecutors;
import org.hyperledger.besu.consensus.ibft.IbftGossip;
import org.hyperledger.besu.consensus.ibft.IbftProcessor;
import org.hyperledger.besu.consensus.ibft.IbftProtocolSchedule;
import org.hyperledger.besu.consensus.ibft.MessageTracker;
import org.hyperledger.besu.consensus.ibft.RoundTimer;
import org.hyperledger.besu.consensus.ibft.UniqueMessageMulticaster;
import org.hyperledger.besu.consensus.ibft.blockcreation.IbftBlockCreatorFactory;
import org.hyperledger.besu.consensus.ibft.blockcreation.IbftMiningCoordinator;
import org.hyperledger.besu.consensus.ibft.blockcreation.ProposerSelector;
import org.hyperledger.besu.consensus.ibft.jsonrpc.IbftJsonRpcMethods;
import org.hyperledger.besu.consensus.ibft.network.ValidatorPeers;
import org.hyperledger.besu.consensus.ibft.payload.MessageFactory;
import org.hyperledger.besu.consensus.ibft.protocol.AgentProtocolManager;
import org.hyperledger.besu.consensus.ibft.protocol.IbftSubProtocol;
import org.hyperledger.besu.consensus.ibft.statemachine.FutureMessageBuffer;
import org.hyperledger.besu.consensus.ibft.statemachine.IbftController;
import org.hyperledger.besu.consensus.ibft.statemachine.IbftBlockHeightManagerFactory;
import org.hyperledger.besu.consensus.ibft.statemachine.IbftFinalState;
import org.hyperledger.besu.consensus.ibft.statemachine.IbftRoundFactory;
import org.hyperledger.besu.consensus.ibft.validation.MessageValidatorFactory;
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
    private final BlockInterface blockInterface = new IbftBlockInterface();

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
                .withSubProtocol(IbftSubProtocol.get(), new AgentProtocolManager(agentEventQueue, peers));

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
        final VoteTallyCache voteTallyCache = protocolContext.getConsensusState(IbftContext.class).getVoteTallyCache();
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

        final IbftGossip gossiper = new IbftGossip(uniqueMessageMulticaster);

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


        final IbftExecutors ibftExecutors = IbftExecutors.create(metricsSystem);
        final IbftBlockCreatorFactory blockCreatorFactory =
                new IbftBlockCreatorFactory(
                        gasLimitCalculator,
                        transactionPool.getAgentPendingTransactions(),
                        protocolContext,
                        protocolSchedule,
                        miningParameters,
                        Util.publicKeyToAddress(nodeKey.getPublicKey()));


        // NOTE: peers should not be used for accessing the network as it does not enforce the
        // "only send once" filter applied by the UniqueMessageMulticaster.


        final IbftFinalState finalState =
                new IbftFinalState(
                        voteTallyCache,
                        nodeKey,
                        Util.publicKeyToAddress(nodeKey.getPublicKey()),
                        proposerSelector,
                        uniqueMessageMulticaster,
                        new RoundTimer(agentEventQueue, agentConfig.getRequestTimeoutSeconds(), ibftExecutors),
                        new BlockTimer(
                                agentEventQueue, agentConfig.getBlockPeriodSeconds(), ibftExecutors, clock),
                        blockCreatorFactory,
                        new MessageFactory(nodeKey),
                        clock);
        final MessageValidatorFactory messageValidatorFactory =
                new MessageValidatorFactory(proposerSelector, protocolSchedule, protocolContext);
        final Subscribers<MinedBlockObserver> minedBlockObservers = Subscribers.create();
        minedBlockObservers.subscribe(ethProtocolManager);
        final FutureMessageBuffer futureMessageBuffer =
                new FutureMessageBuffer(
                        agentConfig.getFutureMessagesMaxDistance(),
                        agentConfig.getFutureMessagesLimit(),
                        blockchain.getChainHeadBlockNumber());

        final IbftController ibftController =
                new IbftController(
                        blockchain,
                        finalState,
                        new IbftBlockHeightManagerFactory(
                                finalState,
                                new IbftRoundFactory(
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


        final EventMultiplexer eventMultiplexer = new EventMultiplexer(ibftController);
        final IbftProcessor ibftProcessor = new IbftProcessor(agentEventQueue, eventMultiplexer);
        final MiningCoordinator ibftMiningCoordinator =
                new IbftMiningCoordinator(
                        ibftExecutors,
                        ibftController,
                        ibftProcessor,
                        blockCreatorFactory,
                        blockchain,
                        agentEventQueue);
        ibftMiningCoordinator.enable();

        return ibftMiningCoordinator;
    }

    //7
    @Override
    protected PluginServiceFactory createAdditionalPluginServices(final Blockchain blockchain) {

        return new IbftQueryPluginServiceFactory(blockchain, nodeKey);
    }

    //2
    @Override
    protected ProtocolSchedule createProtocolSchedule() {

        return IbftProtocolSchedule.create(
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
    protected IbftContext createConsensusContext(
            final Blockchain blockchain, final WorldStateArchive worldStateArchive) {
        final GenesisConfigOptions configOptions =
                genesisConfig.getConfigOptions(genesisConfigOverrides);
        final AgentConfigOptions agentConfig = configOptions.getAgentConfigOptions();
        final EpochManager epochManager = new EpochManager(agentConfig.getEpochLength());
        final Map<Long, List<Address>> ibftValidatorForkMap =
                convertIbftForks(configOptions.getTransitions().getIbftForks());

        return new IbftContext(
                new ForkingVoteTallyCache(
                        blockchain,
                        new VoteTallyUpdater(epochManager, new IbftBlockInterface()),
                        epochManager,
                        new IbftBlockInterface(),
                        new IbftValidatorOverrides(ibftValidatorForkMap)),
                new VoteProposer(),
                epochManager,
                blockInterface);
    }

    //3
    private Map<Long, List<Address>> convertIbftForks(final List<IbftFork> ibftForks) {
        final Map<Long, List<Address>> result = new HashMap<>();

        for (final IbftFork fork : ibftForks) {
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
