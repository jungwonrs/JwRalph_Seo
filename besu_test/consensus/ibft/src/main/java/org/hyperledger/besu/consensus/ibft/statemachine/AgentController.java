package org.hyperledger.besu.consensus.ibft.statemachine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.Gossiper;
import org.hyperledger.besu.consensus.ibft.MessageTracker;
import org.hyperledger.besu.consensus.ibft.SynchronizerUpdater;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentNewChainHead;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentReceivedMessageEvent;

import org.hyperledger.besu.consensus.ibft.ibftevent.AgentBlockTimerExpiry;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentPrepareMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentProposalMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentCommitMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentRoundChangeMessageData;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentMessage;
import org.hyperledger.besu.consensus.ibft.payload.Authored;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.Message;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class AgentController {
    private static final Logger LOG = LogManager.getLogger();
    private final Blockchain blockchain;
    private final AgentFinalState agentFinalState;
    private final AgentBlockHeightManagerFactory agentBlockHeightManagerFactory;
    private final FutureMessageBuffer futureMessageBuffer;
    private AgentBlockHeightManagers currentHeightManager;
    private final Gossiper gossiper;
    private final MessageTracker duplicateMessageTracker;
    private final SynchronizerUpdater synchronizerUpdater;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public AgentController(
            final Blockchain blockchain,
            final AgentFinalState agentFinalState,
            final AgentBlockHeightManagerFactory agentBlockHeightManagerFactory,
            final Gossiper gossiper,
            final MessageTracker duplicateMessageTracker,
            final FutureMessageBuffer futureMessageBuffer,
            final SynchronizerUpdater synchronizerUpdater) {
        this.blockchain = blockchain;
        this.agentFinalState = agentFinalState;
        this.agentBlockHeightManagerFactory = agentBlockHeightManagerFactory;
        this.futureMessageBuffer = futureMessageBuffer;
        this.gossiper = gossiper;
        this.duplicateMessageTracker = duplicateMessageTracker;
        this.synchronizerUpdater = synchronizerUpdater;

    }

    public void start(){
        if (started.compareAndSet(false, true)){
            startNewHeightManager(blockchain.getChainHeadHeader());
        }
    }

    public void handleMessageEvent(final AgentReceivedMessageEvent msg){
        final MessageData data = msg.getMessage().getData();
        if (!duplicateMessageTracker.hasSeenMessage(data)){
            duplicateMessageTracker.addSeenMessage(data);
            handleMessage(msg.getMessage());

        }else {
            LOG.trace("Discarded duplicate message");
        }

    }

    private void handleMessage(final Message message){
        final MessageData messageData = message.getData();
        switch (messageData.getCode()){
          /*  case AgentMessageData.AGENT_ON:
                System.out.println("==========================================hello world!!!!");
                consumeMessage(
                        message,
                        AgentOnMessageData.fromMessageData(messageData).decode(),
                        currentHeightManager::handleAgentOnPayload);
                        break;*/

            case AgentMessageData.PROPOSAL:
                consumeMessage(
                        message,
                        AgentProposalMessageData.fromMessageData(messageData).decode(),
                        currentHeightManager::handleProposalPayload);
                        break;
            case AgentMessageData.PREPARE:
                consumeMessage(
                        message,
                        AgentPrepareMessageData.fromMessageData(messageData).decode(),
                        currentHeightManager::handlePreparePayload);
                        break;
            case AgentMessageData.COMMIT:
                consumeMessage(
                        message,
                        AgentCommitMessageData.fromMessageData(messageData).decode(),
                        currentHeightManager::handleCommitPayload);
                        break;

            case AgentMessageData.ROUND_CHANGE:
                consumeMessage(
                        message,
                        AgentRoundChangeMessageData.fromMessageData(messageData).decode(),
                        currentHeightManager::handleRoundChangePayload);
                        break;

            default:
                throw new IllegalArgumentException(
                        String.format(
                                "Received message with messageCode = %d does not conform to any recognised Agent message structure",
                                message.getData().getCode()));

        }
    }


    private  <P extends AgentMessage<?>> void consumeMessage(
            final Message message, final P agentMessage, final Consumer<P> handleMessage){
        LOG.trace("Received Agent {} message", agentMessage.getClass().getSimpleName());
        if (processMessage(agentMessage, message)){
            gossiper.send(message);
            handleMessage.accept(agentMessage);
        }
    }

    public void handleNewBlockEvent(final AgentNewChainHead newChainHead){
        final BlockHeader newBlockHeader = newChainHead.getNewChainHeadHeader();
        final BlockHeader currentMiningParent = currentHeightManager.getParentBlockHeader();
        LOG.debug(
                "New Chain head detected (block number = {}), " + " currnetly mining on top of {}.",
                newBlockHeader.getNumber(),
                currentMiningParent.getNumber());
        if (newBlockHeader.getNumber() < currentMiningParent.getNumber()){
            LOG.trace(
                    "Discarding NewChainHead event, was for previous block height. chainHeight={} eventHeight={}",
                    currentMiningParent.getNumber(),
                    newBlockHeader.getNumber());
            return;
        }

        if (newBlockHeader.getNumber() == currentMiningParent.getNumber()){
            if (newBlockHeader.getHash().equals(currentMiningParent.getHash())){
                LOG.trace(
                        "Discarding duplicate NewChainHead event. chainHeight={} newBlockHash={} parentBlockHash={}",
                        newBlockHeader.getNumber(),
                        newBlockHeader.getHash(),
                        currentMiningParent.getHash());
            } else {
                LOG.error(
                        "Subsequent NewChainHead event at same block height indicates chain fork. chainHeight = {}",
                        currentMiningParent.getNumber());
            }
            return;
        }
        startNewHeightManager(newBlockHeader);
    }

    public void handleBlockTimerExpiry(final AgentBlockTimerExpiry blockTimerExpiry) {
        final ConsensusRoundIdentifier roundIdentifier = blockTimerExpiry.getRoundIdentifier();
        if (isMsgForCurrentHeight(roundIdentifier)){
            currentHeightManager.handleBlockTimerExpiry(roundIdentifier);
        } else {
            LOG.trace(
                    "Block timer event discarded as it is not for current block height chainHeight={} eventHeight={}",
                    currentHeightManager.getChainHeight(),
                    roundIdentifier.getSequenceNumber());
        }
    }


    private void startNewHeightManager (final BlockHeader parentHeader){
        currentHeightManager = agentBlockHeightManagerFactory.create(parentHeader);
        final long newChainHeight = currentHeightManager.getChainHeight();
        futureMessageBuffer.retrieveMessagesForHeight(newChainHeight).forEach(this::handleMessage);
    }

    private boolean processMessage(final AgentMessage<?> msg, final Message rawMsg){
        final ConsensusRoundIdentifier msgRoundIdentifier = msg.getRoundIdentifier();
        if (isMsgForCurrentHeight(msgRoundIdentifier)){
            return isMsgFromKnownValidator(msg) && agentFinalState.isLocalNodeValidator();
        } else if (isMsgForFutureChainHeight(msgRoundIdentifier)){
            LOG.trace("Received message for future block height round = {}", msgRoundIdentifier);
            futureMessageBuffer.addMessage(msgRoundIdentifier.getSequenceNumber(), rawMsg);

            synchronizerUpdater.updatePeerChainState(
                    msgRoundIdentifier.getSequenceNumber() - 1L, rawMsg.getConnection());
        } else {
            LOG.trace(
                    "AGENT message discarded as it is from a previous block height messageType = {} chainHeight={} eventHeight={}",
                    msg.getMessageType(),
                    currentHeightManager.getChainHeight(),
                    msgRoundIdentifier.getSequenceNumber());
        }
        return false;
    }

    private boolean isMsgFromKnownValidator(final Authored msg){
        return agentFinalState.getValidators().contains(msg.getAuthor());

    }

    private boolean isMsgForCurrentHeight(final ConsensusRoundIdentifier roundIdentifier){
        return roundIdentifier.getSequenceNumber() == currentHeightManager.getChainHeight();
    }

    private boolean isMsgForFutureChainHeight(final ConsensusRoundIdentifier roundIdentifier){
        return  roundIdentifier.getSequenceNumber() > currentHeightManager.getChainHeight();
    }


}
