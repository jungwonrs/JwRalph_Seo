package org.hyperledger.besu.consensus.ibft.ibftevent;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class AgentBlockTimerExpiry implements AgentEvent {
    private final ConsensusRoundIdentifier roundIdentifier;

    public AgentBlockTimerExpiry(final ConsensusRoundIdentifier roundIdentifier) {this.roundIdentifier = roundIdentifier;}

    @Override
    public AgentEvents.Type getType() {return AgentEvents.Type.BLOCK_TIMER_EXPIRY;}


    public ConsensusRoundIdentifier getRoundIdentifier() {return roundIdentifier;}

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("Round Identifier", roundIdentifier).toString();
    }

    @Override
    public boolean equals(final Object o){
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        final AgentBlockTimerExpiry that = (AgentBlockTimerExpiry) o;
        return Objects.equals(roundIdentifier, that.roundIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundIdentifier);
    }
}
