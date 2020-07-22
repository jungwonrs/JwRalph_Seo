package org.hyperledger.besu.consensus.ibft.ibftevent;

import org.hyperledger.besu.ethereum.p2p.rlpx.wire.Message;

public class AgentReceivedMessageEvent implements AgentEvent {

    private final Message message;

    public AgentReceivedMessageEvent(final Message message){
        this.message = message;
    }

    public Message getMessage() {return message;}

    @Override
    public AgentEvents.Type getType(){return AgentEvents.Type.MESSAGE;}
}
