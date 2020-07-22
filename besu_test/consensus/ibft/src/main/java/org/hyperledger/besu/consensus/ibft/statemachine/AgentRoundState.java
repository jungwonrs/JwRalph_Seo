package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.AgentHelper;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.validation.AgentMessageValidator;
import org.hyperledger.besu.crypto.SECP256K1.Signature;
import org.hyperledger.besu.ethereum.core.Block;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AgentRoundState {
    private static final Logger LOG = LogManager.getLogger();

    private final ConsensusRoundIdentifier roundIdentifier;
    private final AgentMessageValidator validator;
    private final long quorum;

    private Optional<AgentProposal> proposalMessage = Optional.empty();

    // Must track the actual Prepare message, not just the sender, as these may need to be reused
    // to send out in a PrepareCertificate.
    private final Set<AgentPrepare> prepareMessages = Sets.newLinkedHashSet();
    private final Set<AgentCommit> commitMessages = Sets.newLinkedHashSet();

    private boolean prepared = false;
    private boolean committed = false;

    public AgentRoundState(
            final ConsensusRoundIdentifier roundIdentifier,
            final int quorum,
            final AgentMessageValidator validator) {
        this.roundIdentifier = roundIdentifier;
        this.quorum = quorum;
        this.validator = validator;
    }

    public ConsensusRoundIdentifier getRoundIdentifier() {
        return roundIdentifier;
    }

    public boolean setProposedBlock(final AgentProposal msg) {

        if (!proposalMessage.isPresent()) {
            if (validator.validateProposal(msg)) {
                proposalMessage = Optional.of(msg);
                prepareMessages.removeIf(p -> !validator.validatePrepare(p));
                commitMessages.removeIf(p -> !validator.validateCommit(p));
                updateState();
                return true;
            }
        }

        return false;
    }

    public void addPrepareMessage(final AgentPrepare msg) {
        if (!proposalMessage.isPresent() || validator.validatePrepare(msg)) {
            prepareMessages.add(msg);
            LOG.trace("Round state added prepare message prepare={}", msg);
        }
        updateState();
    }

    public void addCommitMessage(final AgentCommit msg) {
        if (!proposalMessage.isPresent() || validator.validateCommit(msg)) {
            commitMessages.add(msg);
            LOG.trace("Round state added commit message commit={}", msg);
        }

        updateState();
    }


    private void updateState() {
        // NOTE: The quorum for Prepare messages is 1 less than the quorum size as the proposer
        // does not supply a prepare message
        final long prepareQuorum = AgentHelper.prepareMessageCountForQuorum(quorum);
        prepared = (prepareMessages.size() >= prepareQuorum) && proposalMessage.isPresent();
        committed = (commitMessages.size() >= quorum) && proposalMessage.isPresent();
        LOG.trace(
                "Round state updated prepared={} committed={} preparedQuorum={}/{} committedQuorum={}/{}",
                prepared,
                committed,
                prepareMessages.size(),
                prepareQuorum,
                commitMessages.size(),
                quorum);
    }

    public Optional<Block> getProposedBlock() {
        return proposalMessage.map(AgentProposal::getBlock);
    }

    public boolean isPrepared() {
        return prepared;
    }

    public boolean isCommitted() {
        return committed;
    }

    public Collection<Signature> getCommitSeals() {
        return commitMessages.stream()
                .map(cp -> cp.getSignedPayload().getPayload().getCommitSeal())
                .collect(Collectors.toList());
    }

    public Optional<AgentPreparedRoundArtifacts> constructPreparedRoundArtifacts() {
        if (isPrepared()) {
            return Optional.of(new AgentPreparedRoundArtifacts(proposalMessage.get(), prepareMessages));
        }
        return Optional.empty();
    }
}
