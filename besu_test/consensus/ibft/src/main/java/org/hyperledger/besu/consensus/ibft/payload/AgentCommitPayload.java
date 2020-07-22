package org.hyperledger.besu.consensus.ibft.payload;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.IbftV2;
import org.hyperledger.besu.crypto.SECP256K1;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

import java.util.Objects;
import java.util.StringJoiner;

public class AgentCommitPayload implements AgentPayload {

    private static final int TYPE = AgentMessageData.COMMIT;
    private final ConsensusRoundIdentifier roundIdentifier;
    private final Hash digest;
    private final SECP256K1.Signature commitSeal;

    public AgentCommitPayload(
            final ConsensusRoundIdentifier roundIdentifier,
            final Hash digest,
            final SECP256K1.Signature commitSeal) {
        this.roundIdentifier = roundIdentifier;
        this.digest = digest;
        this.commitSeal = commitSeal;
    }

    public static AgentCommitPayload readFrom(final RLPInput rlpInput) {
        rlpInput.enterList();
        final ConsensusRoundIdentifier roundIdentifier = ConsensusRoundIdentifier.readFrom(rlpInput);
        final Hash digest = Payload.readDigest(rlpInput);
        final SECP256K1.Signature commitSeal = rlpInput.readBytes(SECP256K1.Signature::decode);
        rlpInput.leaveList();

        return new AgentCommitPayload(roundIdentifier, digest, commitSeal);
    }

    @Override
    public void writeTo(final RLPOutput rlpOutput) {
        rlpOutput.startList();
        roundIdentifier.writeTo(rlpOutput);
        rlpOutput.writeBytes(digest);
        rlpOutput.writeBytes(commitSeal.encodedBytes());
        rlpOutput.endList();
    }

    @Override
    public int getMessageType() {
        return TYPE;
    }

    public Hash getDigest() {
        return digest;
    }

    public SECP256K1.Signature getCommitSeal() {
        return commitSeal;
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
        final AgentCommitPayload that = (AgentCommitPayload) o;
        return Objects.equals(roundIdentifier, that.roundIdentifier)
                && Objects.equals(digest, that.digest)
                && Objects.equals(commitSeal, that.commitSeal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundIdentifier, digest, commitSeal);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgentCommitPayload.class.getSimpleName() + "[", "]")
                .add("roundIdentifier=" + roundIdentifier)
                .add("digest=" + digest)
                .add("commitSeal=" + commitSeal)
                .toString();
    }
}