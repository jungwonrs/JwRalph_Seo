package org.hyperledger.besu.consensus.ibft.messagedata;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

public class AgentProposalMessageData extends AbstractAgentMessageData {

    private static final int MESSAGE_CODE = AgentMessageData.PROPOSAL;

    private AgentProposalMessageData(final Bytes data) {
        super(data);
    }

    public static AgentProposalMessageData fromMessageData(final MessageData messageData) {
        return fromMessageData(
                messageData, MESSAGE_CODE, AgentProposalMessageData.class, AgentProposalMessageData::new);
    }

    public AgentProposal decode() {
        return AgentProposal.decode(data);
    }

    public static AgentProposalMessageData create(final AgentProposal proposal) {
        return new AgentProposalMessageData(proposal.encode());
    }

    @Override
    public int getCode() {
        return MESSAGE_CODE;
    }
}

