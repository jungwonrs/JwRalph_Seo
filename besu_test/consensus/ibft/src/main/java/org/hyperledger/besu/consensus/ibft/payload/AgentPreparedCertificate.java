package org.hyperledger.besu.consensus.ibft.payload;

import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class AgentPreparedCertificate {
    private final AgentSignedData<AgentProposalPayload> proposalPayload;
    private final List<AgentSignedData<AgentPreparePayload>> preparePayloads;

    public AgentPreparedCertificate(
            final AgentSignedData<AgentProposalPayload> proposalPayload,
            final List<AgentSignedData<AgentPreparePayload>> preparePayloads) {
        this.proposalPayload = proposalPayload;
        this.preparePayloads = preparePayloads;
    }

    public static AgentPreparedCertificate readFrom(final RLPInput rlpInput) {
        final AgentSignedData<AgentProposalPayload> proposalMessage;
        final List<AgentSignedData<AgentPreparePayload>> prepareMessages;

        rlpInput.enterList();
        proposalMessage = AgentSignedData.readSignedProposalPayloadFrom(rlpInput);
        prepareMessages = rlpInput.readList(AgentSignedData::readSignedPreparePayloadFrom);
        rlpInput.leaveList();

        return new AgentPreparedCertificate(proposalMessage, prepareMessages);
    }

    public void writeTo(final RLPOutput rlpOutput) {
        rlpOutput.startList();
        proposalPayload.writeTo(rlpOutput);
        rlpOutput.writeList(preparePayloads, AgentSignedData::writeTo);
        rlpOutput.endList();
    }

    public AgentSignedData<AgentProposalPayload> getProposalPayload() {
        return proposalPayload;
    }

    public Collection<AgentSignedData<AgentPreparePayload>> getPreparePayloads() {
        return preparePayloads;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgentPreparedCertificate that = (AgentPreparedCertificate) o;
        return Objects.equals(proposalPayload, that.proposalPayload)
                && Objects.equals(preparePayloads, that.preparePayloads);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proposalPayload, preparePayloads);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PreparedCertificate.class.getSimpleName() + "[", "]")
                .add("proposalPayload=" + proposalPayload)
                .add("preparePayloads=" + preparePayloads)
                .toString();
    }
}

