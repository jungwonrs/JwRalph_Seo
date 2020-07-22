package org.hyperledger.besu.consensus.ibft.messagedata;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

public class AgentCommitMessageData extends AbstractAgentMessageData {
    private static final int MESSAGE_CODE = AgentMessageData.COMMIT;

    private AgentCommitMessageData(final Bytes data) {
        super(data);
    }

    public static AgentCommitMessageData fromMessageData(final MessageData messageData) {
        return fromMessageData(
                messageData, MESSAGE_CODE, AgentCommitMessageData.class, AgentCommitMessageData::new);
    }

    public AgentCommit decode() {
        return AgentCommit.decode(data);
    }

    public static AgentCommitMessageData create(final AgentCommit commit) {
        return new AgentCommitMessageData(commit.encode());
    }

    @Override
    public int getCode() {
        return MESSAGE_CODE;
    }
}

