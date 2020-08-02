package org.hyperledger.besu.consensus.ibft.statemachine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


import com.google.common.base.Splitter;
import org.apache.logging.log4j.Logger;

import org.hyperledger.besu.consensus.ibft.IbftBlockHeaderValidationRulesetFactory;
import org.hyperledger.besu.consensus.ibft.IbftExtraData;
import org.hyperledger.besu.consensus.ibft.IbftHelpers;
import org.hyperledger.besu.consensus.ibft.SynchronizerUpdater;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentBlockCreator;
import org.hyperledger.besu.crypto.AgentKeyGenerator;
import org.hyperledger.besu.crypto.AgentSignature;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.chain.MinedBlockObserver;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.Difficulty;
import org.hyperledger.besu.ethereum.eth.manager.EthContext;
import org.hyperledger.besu.ethereum.eth.sync.BlockBroadcaster;
import org.hyperledger.besu.ethereum.eth.transactions.TransactionPool;
import org.hyperledger.besu.ethereum.mainnet.BlockHeaderValidator;
import org.hyperledger.besu.ethereum.mainnet.HeaderValidationMode;
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


    public AgentConsensusController(
            final String agentConfig,
            final List<String> addressList,
            final String nodeAddress,
            final TransactionPool transactionPool,
            final String previousBlock,

            final Blockchain blockchain,
            final IbftFinalState ibftFinalState,
            final Subscribers<MinedBlockObserver> minedBlockObserverSubscribers,
            final int time
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
    }


    public void start(){

        if (started.compareAndSet(false, true)){
   /*         AgentConnection receiver = new AgentConnection();
            receiver.start();
            System.out.println("===================================="+time);*/

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
                        System.out.println("agentStart!!!!!!!!!!!!!!!!!!!!!!!111111111111111!!!!!!!!!!!!!!");

                        firstCheck(out);
                    }

                }


            } catch (Exception e) {
                LOG.error(e+"hello fuck you 222222222222222222");
            }

        }

        private void nodeHandler(final DataInputStream in, final DataOutputStream out){
            while (true){
                try{
                    String messageFromAgent = in.readUTF();

                    if(messageFromAgent.contains("AgentStart")){
                        System.out.println("agentStart!!!!!!!!!!!!!!!!!!!!!!!!222222222222222!!!!!!!!!!!!!!");
                        firstCheck(out);

                    }

                }catch (Exception e){
                    LOG.error(e+"hello fuck you11111111111111");
                }
            }
        }


        private void firstCheck(final DataOutputStream out){









            System.out.println(out);
            final BlockHeader parentHeader = blockchain.getChainHeadHeader();
            System.out.println("============================================================="+parentHeader.getNumber());
            final AgentBlockCreator blockCreator =
                    ibftFinalState.getBlockCreatorFactory().AgentCreate(parentHeader, transactionPool.getPendingTransactions());

            final Block block = blockCreator.createBlock(parentHeader.getTimestamp()+1);
            final Block blockToImport = IbftHelpers.createAgentBlock(block);



            minedBlockObserverSubscribers.forEach(obs -> obs.agentBlockMined(blockToImport));



            /* parentBlockNumber = blockchain.getChainHeadHeader();
            String txPool = transactionPool.getAgentPendingTransactions().toString();

            String txPoolCheckMessage = parentBlockNumber+":"+txPool+":"+out+":"+"Check";

            out.writeUTF(txPoolCheckMessage);*/


            //ibftFinalState.getBlockCreatorFactory().AgentCreate().

        }




    }


}
