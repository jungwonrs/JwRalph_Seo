package org.hyperledger.besu.consensus.ibft.statemachine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


import com.google.common.base.Splitter;
import org.apache.logging.log4j.Logger;



import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.IbftHelpers;
import org.hyperledger.besu.consensus.ibft.blockcreation.IbftBlockCreator;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentBlockCreator;
import org.hyperledger.besu.consensus.ibft.payload.MessageFactory;
import org.hyperledger.besu.consensus.ibft.validation.MessageValidatorFactory;
import org.hyperledger.besu.crypto.AgentKeyGenerator;
import org.hyperledger.besu.crypto.AgentSignature;
import org.hyperledger.besu.crypto.NodeKey;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.chain.MinedBlockObserver;

import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.eth.transactions.PendingTransactions;
import org.hyperledger.besu.ethereum.eth.transactions.TransactionPool;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;
import org.hyperledger.besu.util.Subscribers;




import static org.apache.logging.log4j.LogManager.getLogger;

public class AgentConsensusController {
    private final String agentConfig;
    private final List<String> addressList;
    private final String nodeAddress;
    private final TransactionPool transactionPool;
    private final String previousBlock;
    private final Blockchain blockchain;
    private final IbftFinalState ibftFinalState;
    private final Subscribers<MinedBlockObserver> minedBlockObserverSubscribers;
    private final int time;
    private final AtomicBoolean started = new AtomicBoolean(false);

    private static final Logger LOG = getLogger();

    private final NodeKey nodeKey;
    private final MessageValidatorFactory messageValidatorFactory;
    private final ProtocolContext protocolContext;
    private final ProtocolSchedule protocolSchedule;

    public AgentConsensusController(
            final String agentConfig,
            final List<String> addressList,
            final String nodeAddress,
            final TransactionPool transactionPool,
            final String previousBlock,

            final Blockchain blockchain,
            final IbftFinalState ibftFinalState,
            final Subscribers<MinedBlockObserver> minedBlockObserverSubscribers,
            final int time,

            final ProtocolContext protocolContext,
            final ProtocolSchedule protocolSchedule,
            final NodeKey nodeKey,
            final MessageValidatorFactory messageValidatorFactory

            )
    {
        this.agentConfig = agentConfig;
        this.addressList = addressList;
        this.nodeAddress = nodeAddress;
        this.transactionPool = transactionPool;
        this.previousBlock = previousBlock;

        this.blockchain = blockchain;
        this.ibftFinalState = ibftFinalState;
        this.minedBlockObserverSubscribers = minedBlockObserverSubscribers;
        this.time = time*1000;

        this.nodeKey = nodeKey;
        this.messageValidatorFactory = messageValidatorFactory;
        this.protocolContext = protocolContext;
        this.protocolSchedule = protocolSchedule;
    }

    public void start(){

        if (started.compareAndSet(false, true)){

        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                AgentConnection receiver = new AgentConnection();
                receiver.start();

            }
        };
        t.scheduleAtFixedRate(tt,0,time);
        }



    }

    private class AgentConnection extends Thread{

        @Override
        public void run(){
            connectToAgent();
        }

        private void connectToAgent(){


            Random rand = new Random();
            int seed = previousBlock.hashCode();
            rand.setSeed(seed);
            int randomResult = rand.nextInt(addressList.size());
            String keyNode = addressList.get(randomResult);


            List<String> agentInfo = Splitter.on(":").splitToList(agentConfig);
            String ip = agentInfo.get(0);
            int port = Integer.valueOf(agentInfo.get(1));

            try{
                Socket s = new Socket(ip, port);
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                if(keyNode.equals(nodeAddress)){
                    keyNodeHandler(in, out, randomResult);
                }
                else{
                    nodeHandler(in, out);
                }

            }catch (Exception e){
                LOG.error(e);
            }
        }

        private void keyNodeHandler(final DataInputStream in, final DataOutputStream out, final int randomResult){
            AgentKeyGenerator aKey = new AgentKeyGenerator();
            AgentSignature agentSignature = new AgentSignature();

            try {
                KeyPair getKey = aKey.genKey();
                PrivateKey keyNodePrivateKey = getKey.getPrivate();
                PublicKey keyNodePublicKey = getKey.getPublic();

                String strKeyNodePublicKey = aKey.pubKeyToString(keyNodePublicKey);
                String randomSize = String.valueOf(addressList.size());
                String strRandomResult = String.valueOf(randomResult);

                String rawMessage = previousBlock+":"+randomSize+":"+strRandomResult;
                String agentOnSignature = agentSignature.genSig(keyNodePrivateKey, rawMessage);
                String agentOnMessage = strKeyNodePublicKey+":"+rawMessage+":"+agentOnSignature+":"+"AgentOn";

                out.writeUTF(agentOnMessage);


                while(true){
                    String messageFromAgent = in.readUTF();

                    if(messageFromAgent.contains("AgentOn")) {
                        List<String> parsingAgentMessage = Splitter.on(":").splitToList(messageFromAgent);
                        String agentStrMessage = parsingAgentMessage.get(0);
                        String agentStrPubKey = parsingAgentMessage.get(1);
                        String agentStrSignature = parsingAgentMessage.get(2);

                        PublicKey agentPubKey = aKey.stringToPublicKey(agentStrPubKey);
                        boolean sigVerify = agentSignature.verify(agentStrMessage, agentStrSignature, agentPubKey);

                        if(sigVerify){
                            out.writeUTF("AgentStart");
                        }
                    }
                    if(messageFromAgent.contains("AgentStart")){
                        sendTransactionPool(out);
                    }
                    if(messageFromAgent.contains("FirstResult")){
                        boolean check = checkSignature(messageFromAgent);

                        if(check){
                            out.writeUTF("FirstResultTrue");
                        }
                    }
                    if(messageFromAgent.contains("FirstResultTrue")){
                        createBlockbyPreprocessing();
                    }

                    if(messageFromAgent.contains("TroubleTransaction")){
                        troubleTransactionConsensus(out, messageFromAgent);
                    }

                    if(messageFromAgent.contains("SecondResult")){
                        boolean check = checkSignature(messageFromAgent);

                        if(check){
                            out.writeUTF("SecondResultTrue");
                        }
                    }

                    if (messageFromAgent.contains("SecondResultTrue")) {
                        createBlockbyPreprocessing();
                    }

                }

            } catch (Exception e) {
                LOG.error(e);
            }
        }

        private void nodeHandler(final DataInputStream in, final DataOutputStream out){
            while (true){
                try{
                    String messageFromAgent = in.readUTF();

                    if(messageFromAgent.contains("AgentStart")){
                        sendTransactionPool(out);
                    }

                    if(messageFromAgent.contains("FirstResultTrue")){
                        createBlockbyPreprocessing();
                    }
                    if (messageFromAgent.contains("SecondResultTrue")) {
                        createBlockbyPreprocessing();
                    }

                }catch (Exception e){
                    LOG.error(e);
                }
            }
        }

        private void troubleTransactionConsensus(final DataOutputStream out, final String data){
            List<String> strPar = Splitter.on(":").splitToList(data);

            PendingTransactions troubleTransaction = transactionPool.getTroubleTransactions(strPar.get(0));

            final BlockHeader parentHeader = blockchain.getChainHeadHeader();

            final IbftBlockCreator blockCreator =
                    ibftFinalState.getBlockCreatorFactory().forStepTwo(parentHeader, troubleTransaction);



            final ConsensusRoundIdentifier roundIdentifier = new ConsensusRoundIdentifier(1, 0);
            final MessageFactory messageFactory = new MessageFactory(nodeKey);


            final RoundState roundState = new RoundState(roundIdentifier, 3,   messageValidatorFactory.AgentCreateMessageValidator(roundIdentifier, parentHeader, true));

            final IbftRound round =
                    new IbftRound(
                            roundState,
                            blockCreator,
                            protocolContext,
                            protocolSchedule.getByBlockNumber(roundIdentifier.getSequenceNumber()).getBlockImporter(),
                            minedBlockObserverSubscribers,
                            nodeKey,
                            messageFactory,
                            ibftFinalState.getTransmitter(),
                            ibftFinalState.getRoundTimer(),
                            true
                            );

           round.createAndSendProposalMessage(parentHeader.getTimestamp()+time);

           try{
           if(round.secondStepResult()){
               out.writeUTF("troubleTransactionTrue"); }
           else {
               out.writeUTF("troubleTransactionFalse"); }
            }
            catch (Exception e){
                LOG.error(e);
            }



        }


        private void sendTransactionPool(final DataOutputStream out){
            AgentKeyGenerator key = new AgentKeyGenerator();
            AgentSignature agentSignature = new AgentSignature();

            String parentBlockNumber = String.valueOf(blockchain.getChainHeadHeader().getNumber());
            String txPool = transactionPool.getAgentPendingTransactions().toString();


            try{
                String rawMessage = parentBlockNumber+"@"+txPool+"@"+out;

                KeyPair getKey = key.genKey();
                PrivateKey pKey = getKey.getPrivate();
                PublicKey pubKey = getKey.getPublic();

                String strPubKey = key.pubKeyToString(pubKey);
                String txPoolSig = agentSignature.genSig(pKey, rawMessage);

                String message = strPubKey+":"+rawMessage+":"+txPoolSig+":"+"Check";

                out.writeUTF(message);

            } catch(Exception e){
                LOG.error(e);
            }

        }

        private boolean checkSignature(final String data){

            List<String> strPar = Splitter.on(":").splitToList(data);

            String strPubKey = strPar.get(0);
            String message = strPar.get(1);
            String sig = strPar.get(2);

            AgentKeyGenerator key = new AgentKeyGenerator();
            AgentSignature agentSignature = new AgentSignature();

            try{
                PublicKey pubKey = key.stringToPublicKey(strPubKey);
                boolean sigVerify = agentSignature.verify(message, sig, pubKey);

                if(sigVerify){
                    return true;
                }

            }catch (Exception e){
                LOG.error(e);
                return false;
            }
            return false;
        }

        private void createBlockbyPreprocessing(){

            LOG.trace("Create AgentBlock by firstCheck");

            final BlockHeader parentHeader = blockchain.getChainHeadHeader();
            final AgentBlockCreator blockCreator =
                    ibftFinalState.getBlockCreatorFactory().AgentCreate(parentHeader, transactionPool.getPendingTransactions());

            final Block block = blockCreator.createBlock(parentHeader.getTimestamp()+time);
            final Block blockToImport = IbftHelpers.createAgentBlock(block);


            minedBlockObserverSubscribers.forEach(obs -> obs.agentBlockMined(blockToImport));
        }










    }
}
