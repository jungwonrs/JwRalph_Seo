package org.hyperledger.besu.consensus.ibft.messagedata;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

public class AgentPrepareMessageData extends AbstractAgentMessageData{
    private static final int MESSAGE_CODE = AgentMessageData.PREPARE;

    private AgentPrepareMessageData(final Bytes data) {
        super(data);
    }

    public static AgentPrepareMessageData fromMessageData(final MessageData messageData) {
        return fromMessageData(
                messageData, MESSAGE_CODE, AgentPrepareMessageData.class, AgentPrepareMessageData::new);
    }

    public AgentPrepare decode() {
        return AgentPrepare.decode(data);
    }

    public static AgentPrepareMessageData create(final AgentPrepare preapare) {
        return new AgentPrepareMessageData(preapare.encode());
    }

    @Override
    public int getCode() {
        return MESSAGE_CODE;
    }
}

