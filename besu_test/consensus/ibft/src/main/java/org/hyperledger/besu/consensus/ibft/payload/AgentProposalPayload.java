package org.hyperledger.besu.consensus.ibft.payload;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentMessageData;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

import java.util.Objects;
import java.util.StringJoiner;

public class AgentProposalPayload implements AgentPayload {
    private static final int TYPE = AgentMessageData.PROPOSAL;
    private final ConsensusRoundIdentifier roundIdentifier;
    private final Hash digest;

    public AgentProposalPayload(final ConsensusRoundIdentifier roundIdentifier, final Hash digest) {
        this.roundIdentifier = roundIdentifier;
        this.digest = digest;
    }

    public static AgentProposalPayload readFrom(final RLPInput rlpInput) {
        rlpInput.enterList();
        final ConsensusRoundIdentifier roundIdentifier = ConsensusRoundIdentifier.readFrom(rlpInput);
        final Hash digest = Hash.wrap(rlpInput.readBytes32());
        rlpInput.leaveList();

        return new AgentProposalPayload(roundIdentifier, digest);
    }

    @Override
    public void writeTo(final RLPOutput rlpOutput) {
        rlpOutput.startList();
        roundIdentifier.writeTo(rlpOutput);
        rlpOutput.writeBytes(digest);
        rlpOutput.endList();
    }

    public Hash getDigest() {
        return digest;
    }

    @Override
    public int getMessageType() {
        return TYPE;
    }

    @Override
    public ConsensusRoundIdentifier getRoundIdentifier() {
        return roundIdentifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgentProposalPayload that = (AgentProposalPayload) o;
        return Objects.equals(roundIdentifier, that.roundIdentifier)
                && Objects.equals(digest, that.digest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundIdentifier, digest);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgentProposalPayload.class.getSimpleName() + "[", "]")
                .add("roundIdentifier=" + roundIdentifier)
                .add("digest=" + digest)
                .toString();
    }
}
