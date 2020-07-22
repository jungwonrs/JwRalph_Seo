package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;

import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.payload.AgentPreparedCertificate;
import org.hyperledger.besu.ethereum.core.Block;

import java.util.Collection;
import java.util.stream.Collectors;

public class AgentPreparedRoundArtifacts {
    private final AgentProposal proposal;
    private final Collection<AgentPrepare> prepares;

    public AgentPreparedRoundArtifacts(final AgentProposal proposal, final Collection<AgentPrepare> prepares) {
        this.proposal = proposal;
        this.prepares = prepares;
    }

    public Block getBlock() {
        return proposal.getBlock();
    }

    public AgentPreparedCertificate getPreparedCertificate() {
        return new AgentPreparedCertificate(
                proposal.getSignedPayload(),
                prepares.stream().map(AgentPrepare::getSignedPayload).collect(Collectors.toList()));
    }
}
