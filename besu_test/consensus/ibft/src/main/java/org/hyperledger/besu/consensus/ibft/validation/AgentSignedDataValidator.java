package org.hyperledger.besu.consensus.ibft.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.payload.AgentCommitPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentPreparePayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentProposalPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.consensus.ibft.payload.AgentPayload;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.core.Util;

import java.util.Collection;
import java.util.Optional;

public class AgentSignedDataValidator {
    private static final Logger LOG = LogManager.getLogger();

    private final Collection<Address> validators;
    private final Address expectedProposer;
    private final ConsensusRoundIdentifier roundIdentifier;

    private Optional<AgentSignedData<AgentProposalPayload>> proposal = Optional.empty();

    public AgentSignedDataValidator(
            final Collection<Address> validators,
            final Address expectedProposer,
            final ConsensusRoundIdentifier roundIdentifier) {
        this.validators = validators;
        this.expectedProposer = expectedProposer;
        this.roundIdentifier = roundIdentifier;
    }

    public boolean validateProposal(final AgentSignedData<AgentProposalPayload> msg) {

        if (proposal.isPresent()) {
            return handleSubsequentProposal(proposal.get(), msg);
        }

        if (!validateProposalSignedDataPayload(msg)) {
            return false;
        }

        proposal = Optional.of(msg);
        return true;
    }

    private boolean validateProposalSignedDataPayload(final AgentSignedData<AgentProposalPayload> msg) {

        if (!msg.getPayload().getRoundIdentifier().equals(roundIdentifier)) {
            LOG.info("Invalid Proposal message, does not match current round.");
            return false;
        }

        if (!msg.getAuthor().equals(expectedProposer)) {
            LOG.info(
                    "Invalid Proposal message, was not created by the proposer expected for the "
                            + "associated round.");
            return false;
        }

        return true;
    }

    private boolean handleSubsequentProposal(
            final AgentSignedData<AgentProposalPayload> existingMsg, final AgentSignedData<AgentProposalPayload> newMsg) {
        if (!existingMsg.getAuthor().equals(newMsg.getAuthor())) {
            LOG.info("Received subsequent invalid Proposal message; sender differs from original.");
            return false;
        }

        final AgentProposalPayload existingData = existingMsg.getPayload();
        final AgentProposalPayload newData = newMsg.getPayload();

        if (!proposalMessagesAreIdentical(existingData, newData)) {
            LOG.info("Received subsequent invalid Proposal message; content differs from original.");
            return false;
        }

        return true;
    }

    public boolean validatePrepare(final AgentSignedData<AgentPreparePayload> msg) {
        final String msgType = "Prepare";

        if (!isMessageForCurrentRoundFromValidatorAndProposalAvailable(msg, msgType)) {
            return false;
        }

        if (msg.getAuthor().equals(expectedProposer)) {
            LOG.info("Illegal Prepare message; was sent by the round's proposer.");
            return false;
        }

        return validateDigestMatchesProposal(msg.getPayload().getDigest(), msgType);
    }

    public boolean validateCommit(final AgentSignedData<AgentCommitPayload> msg) {
        final String msgType = "Commit";

        if (!isMessageForCurrentRoundFromValidatorAndProposalAvailable(msg, msgType)) {
            return false;
        }

        final Hash proposedBlockDigest = proposal.get().getPayload().getDigest();
        final Address commitSealCreator =
                Util.signatureToAddress(msg.getPayload().getCommitSeal(), proposedBlockDigest);

        if (!commitSealCreator.equals(msg.getAuthor())) {
            LOG.info("Invalid Commit message. Seal was not created by the message transmitter.");
            return false;
        }

        return validateDigestMatchesProposal(msg.getPayload().getDigest(), msgType);
    }



    private boolean isMessageForCurrentRoundFromValidatorAndProposalAvailable(
            final AgentSignedData<? extends AgentPayload> msg, final String msgType) {

        if (!msg.getPayload().getRoundIdentifier().equals(roundIdentifier)) {
            LOG.info("Invalid {} message, does not match current round.", msgType);
            return false;
        }

        if (!validators.contains(msg.getAuthor())) {
            LOG.info(
                    "Invalid {} message, was not transmitted by a validator for the " + "associated round.",
                    msgType);
            return false;
        }

        if (!proposal.isPresent()) {
            LOG.info(
                    "Unable to validate {} message. No Proposal exists against which to validate "
                            + "block digest.",
                    msgType);
            return false;
        }
        return true;
    }

    private boolean validateDigestMatchesProposal(final Hash digest, final String msgType) {
        final Hash proposedBlockDigest = proposal.get().getPayload().getDigest();
        if (!digest.equals(proposedBlockDigest)) {
            LOG.info(
                    "Illegal {} message, digest does not match the digest in the Prepare Message.", msgType);
            return false;
        }
        return true;
    }

    private boolean proposalMessagesAreIdentical(
            final AgentProposalPayload right, final AgentProposalPayload left) {
        return right.getDigest().equals(left.getDigest())
                && right.getRoundIdentifier().equals(left.getRoundIdentifier());
    }
}


