package org.hyperledger.besu.consensus.ibft.messagedata;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

public class AgentRoundChangeMessageData extends AbstractAgentMessageData {
    private static final int MESSAGE_CODE = AgentMessageData.ROUND_CHANGE;

    private AgentRoundChangeMessageData(final Bytes data) {
        super(data);
    }

    public static AgentRoundChangeMessageData fromMessageData(final MessageData messageData) {
        return fromMessageData(
                messageData, MESSAGE_CODE, AgentRoundChangeMessageData.class, AgentRoundChangeMessageData::new);
    }

    public AgentRoundChange decode() {
        return AgentRoundChange.decode(data);
    }

    public static AgentRoundChangeMessageData create(final AgentRoundChange signedPayload) {

        return new AgentRoundChangeMessageData(signedPayload.encode());
    }

    @Override
    public int getCode() {
        return MESSAGE_CODE;
    }
}

