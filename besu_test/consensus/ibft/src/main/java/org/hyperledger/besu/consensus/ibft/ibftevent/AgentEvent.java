package org.hyperledger.besu.consensus.ibft.ibftevent;

public interface AgentEvent {
    AgentEvents.Type getType();
}
