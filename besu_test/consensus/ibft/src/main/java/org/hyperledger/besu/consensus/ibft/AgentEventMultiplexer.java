package org.hyperledger.besu.consensus.ibft;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentBlockTimerExpiry;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentEvent;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentNewChainHead;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentReceivedMessageEvent;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentRoundExpiry;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentController;

public class AgentEventMultiplexer {
    private static final Logger LOG = LogManager.getLogger();

    private final AgentController agentController;

    public AgentEventMultiplexer(final AgentController agentController) {this.agentController = agentController;}

    public void handleAgentEvent(final AgentEvent agentEvent){
        try{
            switch (agentEvent.getType()){
                case MESSAGE:
                    final AgentReceivedMessageEvent rxEvent = (AgentReceivedMessageEvent) agentEvent;
                    agentController.handleMessageEvent(rxEvent);
                    break;
                case ROUND_EXPIRY:
                    final AgentRoundExpiry roundExpiry = (AgentRoundExpiry) agentEvent;
                    agentController.handleRoundExpiry(roundExpiry);
                    break;
                case NEW_CHAIN_HEAD:
                    final AgentNewChainHead newChainHead = (AgentNewChainHead) agentEvent;
                    agentController.handleNewBlockEvent(newChainHead);
                    break;
                case BLOCK_TIMER_EXPIRY:
                    final AgentBlockTimerExpiry blockTimerExpiry = (AgentBlockTimerExpiry) agentEvent;
                    agentController.handleBlockTimerExpiry(blockTimerExpiry);
                    break;
                default:
                    throw new  RuntimeException("Illegal event in queue.");
            }
        } catch (final Exception e){
            LOG.error("State machine threw exception while processing event {" + agentEvent + "}", e);
        }
    }
}
