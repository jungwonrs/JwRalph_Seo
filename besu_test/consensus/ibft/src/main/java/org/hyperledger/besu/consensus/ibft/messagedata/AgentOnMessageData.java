package org.hyperledger.besu.consensus.ibft.messagedata;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentOn;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

public class AgentOnMessageData extends AbstractAgentMessageData {
    private static final int MESSAGE_CODE = AgentMessageData.AGENT_ON;

    private AgentOnMessageData(final Bytes data) {
        super(data);
    }

    public static AgentOnMessageData fromMessageData(final MessageData messageData) {
        return fromMessageData(
                messageData, MESSAGE_CODE, AgentOnMessageData.class, AgentOnMessageData::new);
    }

    public AgentOn decode() {
        return AgentOn.decode(data);
    }

    public static AgentOnMessageData create(final AgentOn on) {
        return new AgentOnMessageData(on.encode());
    }

    @Override
    public int getCode() {
        return MESSAGE_CODE;
    }
}