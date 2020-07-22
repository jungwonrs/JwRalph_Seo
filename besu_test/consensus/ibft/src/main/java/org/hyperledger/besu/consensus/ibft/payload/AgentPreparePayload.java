package org.hyperledger.besu.consensus.ibft.payload;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.IbftV2;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

import java.util.Objects;
import java.util.StringJoiner;

public class AgentPreparePayload implements AgentPayload {
    private static final int TYPE = AgentMessageData.PREPARE;
    private final ConsensusRoundIdentifier roundIdentifier;
    private final Hash digest;

    public AgentPreparePayload(final ConsensusRoundIdentifier roundIdentifier, final Hash digest) {
        this.roundIdentifier = roundIdentifier;
        this.digest = digest;
    }

    public static AgentPreparePayload readFrom(final RLPInput rlpInput) {
        rlpInput.enterList();
        final ConsensusRoundIdentifier roundIdentifier = ConsensusRoundIdentifier.readFrom(rlpInput);
        final Hash digest = Payload.readDigest(rlpInput);
        rlpInput.leaveList();
        return new AgentPreparePayload(roundIdentifier, digest);
    }

    @Override
    public void writeTo(final RLPOutput rlpOutput) {
        rlpOutput.startList();
        roundIdentifier.writeTo(rlpOutput);
        rlpOutput.writeBytes(digest);
        rlpOutput.endList();
    }

    @Override
    public int getMessageType() {
        return TYPE;
    }

    public Hash getDigest() {
        return digest;
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
        final AgentPreparePayload that = (AgentPreparePayload) o;
        return Objects.equals(roundIdentifier, that.roundIdentifier)
                && Objects.equals(digest, that.digest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundIdentifier, digest);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgentPreparePayload.class.getSimpleName() + "[", "]")
                .add("roundIdentifier=" + roundIdentifier)
                .add("digest=" + digest)
                .toString();
    }
}
