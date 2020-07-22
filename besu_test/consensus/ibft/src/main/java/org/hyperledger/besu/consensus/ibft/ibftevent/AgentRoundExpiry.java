package org.hyperledger.besu.consensus.ibft.ibftevent;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class AgentRoundExpiry implements AgentEvent {
    private final ConsensusRoundIdentifier round;

    public AgentRoundExpiry(final ConsensusRoundIdentifier round){this.round = round;}

    @Override
    public AgentEvents.Type getType() {return AgentEvents.Type.ROUND_EXPIRY;}

    public ConsensusRoundIdentifier getView() {return round;}

    @Override
    public String toString() {return  MoreObjects.toStringHelper(this).add("Round", round).toString();}

    @Override
    public boolean equals(final Object o){
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        final AgentRoundExpiry that = (AgentRoundExpiry) o;
        return Objects.equals(round, that.round);
    }

    @Override
    public int hashCode() {return Objects.hash(round);}

}
