package org.hyperledger.besu.consensus.ibft.payload;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentMessageData;
import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class AgentRoundChangePayload implements AgentPayload {
    private static final int TYPE = AgentMessageData.ROUND_CHANGE;
    private final ConsensusRoundIdentifier roundChangeIdentifier;

    // The validator may not hae any prepared certificate
    private final Optional<AgentPreparedCertificate> preparedCertificate;

    public AgentRoundChangePayload(
            final ConsensusRoundIdentifier roundIdentifier,
            final Optional<AgentPreparedCertificate> preparedCertificate) {
        this.roundChangeIdentifier = roundIdentifier;
        this.preparedCertificate = preparedCertificate;
    }

    @Override
    public ConsensusRoundIdentifier getRoundIdentifier() {
        return roundChangeIdentifier;
    }

    public Optional<AgentPreparedCertificate> getPreparedCertificate() {
        return preparedCertificate;
    }

    @Override
    public void writeTo(final RLPOutput rlpOutput) {
        // RLP encode of the message data content (round identifier and prepared certificate)
        rlpOutput.startList();
        roundChangeIdentifier.writeTo(rlpOutput);

        if (preparedCertificate.isPresent()) {
            preparedCertificate.get().writeTo(rlpOutput);
        } else {
            rlpOutput.writeNull();
        }
        rlpOutput.endList();
    }

    public static AgentRoundChangePayload readFrom(final RLPInput rlpInput) {
        rlpInput.enterList();
        final ConsensusRoundIdentifier roundIdentifier = ConsensusRoundIdentifier.readFrom(rlpInput);

        final Optional<AgentPreparedCertificate> preparedCertificate;

        if (rlpInput.nextIsNull()) {
            rlpInput.skipNext();
            preparedCertificate = Optional.empty();
        } else {
            preparedCertificate = Optional.of(AgentPreparedCertificate.readFrom(rlpInput));
        }
        rlpInput.leaveList();

        return new AgentRoundChangePayload(roundIdentifier, preparedCertificate);
    }

    @Override
    public int getMessageType() {
        return TYPE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgentRoundChangePayload that = (AgentRoundChangePayload) o;
        return Objects.equals(roundChangeIdentifier, that.roundChangeIdentifier)
                && Objects.equals(preparedCertificate, that.preparedCertificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundChangeIdentifier, preparedCertificate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RoundChangePayload.class.getSimpleName() + "[", "]")
                .add("roundChangeIdentifier=" + roundChangeIdentifier)
                .add("preparedCertificate=" + preparedCertificate)
                .toString();
    }
}
