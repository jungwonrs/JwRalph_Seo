package org.hyperledger.besu.consensus.ibft.ibftevent;

import org.hyperledger.besu.ethereum.core.BlockHeader;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class AgentNewChainHead implements AgentEvent{
    private final BlockHeader newChainHeadHeader;

    public AgentNewChainHead(final BlockHeader newChainHeadHeader) {this.newChainHeadHeader = newChainHeadHeader;}

    @Override
    public AgentEvents.Type getType(){return AgentEvents.Type.NEW_CHAIN_HEAD;}

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("New Chain Head Header", newChainHeadHeader)
                .toString();
    }

    @Override
    public boolean equals(final Object o){
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        final AgentNewChainHead that = (AgentNewChainHead) o;
        return Objects.equals(newChainHeadHeader, that.newChainHeadHeader);
    }

    @Override
    public int hashCode() {return Objects.hash(newChainHeadHeader);}

    public BlockHeader getNewChainHeadHeader() {return newChainHeadHeader;}




}
