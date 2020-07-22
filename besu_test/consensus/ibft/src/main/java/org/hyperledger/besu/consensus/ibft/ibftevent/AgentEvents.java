package org.hyperledger.besu.consensus.ibft.ibftevent;

import org.hyperledger.besu.ethereum.p2p.rlpx.wire.Message;

public class AgentEvents {
    public static AgentEvent fromMessage(final Message message) {
        return new AgentReceivedMessageEvent(message);
    }

    public enum Type{
        ROUND_EXPIRY,
        NEW_CHAIN_HEAD,
        BLOCK_TIMER_EXPIRY,
        MESSAGE
    }


}
