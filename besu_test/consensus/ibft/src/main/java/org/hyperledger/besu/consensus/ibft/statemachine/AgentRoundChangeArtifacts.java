package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangeCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangePayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.ethereum.core.Block;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgentRoundChangeArtifacts {
    private final Optional<Block> block;
    private final List<AgentSignedData<AgentRoundChangePayload>> roundChangePayloads;

    public AgentRoundChangeArtifacts(
            final Optional<Block> block, final List<AgentSignedData<AgentRoundChangePayload>> roundChangePayloads) {
        this.block = block;
        this.roundChangePayloads = roundChangePayloads;
    }

    public Optional<Block> getBlock() {
        return block;
    }

    public AgentRoundChangeCertificate getRoundChangeCertificate() {
        return new AgentRoundChangeCertificate(roundChangePayloads);
    }

    public static AgentRoundChangeArtifacts create(final Collection<AgentRoundChange> roundChanges) {

        final Comparator<AgentRoundChange> preparedRoundComparator =
                (o1, o2) -> {
                    if (!o1.getPreparedCertificateRound().isPresent()) {
                        return -1;
                    }
                    if (!o2.getPreparedCertificateRound().isPresent()) {
                        return 1;
                    }
                    return o1.getPreparedCertificateRound()
                            .get()
                            .compareTo(o2.getPreparedCertificateRound().get());
                };

        final List<AgentSignedData<AgentRoundChangePayload>> payloads =
                roundChanges.stream().map(AgentRoundChange::getSignedPayload).collect(Collectors.toList());

        final Optional<AgentRoundChange> roundChangeWithNewestPrepare =
                roundChanges.stream().max(preparedRoundComparator);

        final Optional<Block> proposedBlock =
                roundChangeWithNewestPrepare.flatMap(AgentRoundChange::getProposedBlock);
        return new AgentRoundChangeArtifacts(proposedBlock, payloads);
    }
}