package org.hyperledger.besu.consensus.ibft.payload;

import com.google.common.collect.Lists;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.consensus.ibft.messagewrappers.RoundChange;
import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class AgentRoundChangeCertificate {
    private final List<AgentSignedData<AgentRoundChangePayload>> roundChangePayloads;

    public AgentRoundChangeCertificate(final List<AgentSignedData<AgentRoundChangePayload>> roundChangePayloads) {
        this.roundChangePayloads = roundChangePayloads;
    }

    public static AgentRoundChangeCertificate readFrom(final RLPInput rlpInput) {
        final List<AgentSignedData<AgentRoundChangePayload>> roundChangePayloads;

        rlpInput.enterList();
        roundChangePayloads = rlpInput.readList(AgentSignedData::readSignedRoundChangePayloadFrom);
        rlpInput.leaveList();

        return new AgentRoundChangeCertificate(roundChangePayloads);
    }

    public void writeTo(final RLPOutput rlpOutput) {
        rlpOutput.startList();
        rlpOutput.writeList(roundChangePayloads, AgentSignedData::writeTo);
        rlpOutput.endList();
    }

    public Collection<AgentSignedData<AgentRoundChangePayload>> getRoundChangePayloads() {
        return roundChangePayloads;
    }

    public static class Builder {

        private final List<AgentRoundChange> roundChangePayloads = Lists.newArrayList();

        public Builder() {}

        public void appendRoundChangeMessage(final AgentRoundChange msg) {
            roundChangePayloads.add(msg);
        }

        public AgentRoundChangeCertificate buildCertificate() {
            return new AgentRoundChangeCertificate(
                    roundChangePayloads.stream()
                            .map(AgentRoundChange::getSignedPayload)
                            .collect(Collectors.toList()));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgentRoundChangeCertificate that = (AgentRoundChangeCertificate) o;
        return Objects.equals(roundChangePayloads, that.roundChangePayloads);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundChangePayloads);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgentRoundChangeCertificate.class.getSimpleName() + "[", "]")
                .add("roundChangePayloads=" + roundChangePayloads)
                .toString();
    }
}