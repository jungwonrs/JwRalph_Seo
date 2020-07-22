package org.hyperledger.besu.consensus.ibft.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangeCertificate;
import org.hyperledger.besu.ethereum.BlockValidator;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.mainnet.HeaderValidationMode;

import java.util.Optional;

public class AgentMessageValidator {

    private static final Logger LOG = LogManager.getLogger();

    private final AgentSignedDataValidator signedDataValidator;
    private final AgentProposalBlockConsistencyValidator proposalConsistencyValidator;
    private final BlockValidator blockValidator;
    private final ProtocolContext protocolContext;
    private final AgentRoundChangeCertificateValidator roundChangeCertificateValidator;

    public AgentMessageValidator(
            final AgentSignedDataValidator signedDataValidator,
            final AgentProposalBlockConsistencyValidator proposalConsistencyValidator,
            final BlockValidator blockValidator,
            final ProtocolContext protocolContext,
            final AgentRoundChangeCertificateValidator roundChangeCertificateValidator) {
        this.signedDataValidator = signedDataValidator;
        this.proposalConsistencyValidator = proposalConsistencyValidator;
        this.blockValidator = blockValidator;
        this.protocolContext = protocolContext;
        this.roundChangeCertificateValidator = roundChangeCertificateValidator;
    }

    public boolean validateProposal(final AgentProposal msg) {

        if (!signedDataValidator.validateProposal(msg.getSignedPayload())) {
            LOG.info("Illegal Proposal message, embedded signed data failed validation");
            return false;
        }

        if (!validateBlock(msg.getBlock())) {
            return false;
        }

        if (!validateProposalAndRoundChangeAreConsistent(msg)) {
            LOG.info("Illegal Proposal message, embedded roundChange does not match proposal.");
            return false;
        }

        return proposalConsistencyValidator.validateProposalMatchesBlock(
                msg.getSignedPayload(), msg.getBlock());
    }

    private boolean validateBlock(final Block block) {
        final Optional<BlockValidator.BlockProcessingOutputs> validationResult =
                blockValidator.validateAndProcessBlock(
                        protocolContext, block, HeaderValidationMode.LIGHT, HeaderValidationMode.FULL);

        if (!validationResult.isPresent()) {
            LOG.info("Invalid Proposal message, block did not pass validation.");
            return false;
        }

        return true;
    }

    private boolean validateProposalAndRoundChangeAreConsistent(final AgentProposal proposal) {
        final ConsensusRoundIdentifier proposalRoundIdentifier = proposal.getRoundIdentifier();

        if (proposalRoundIdentifier.getRoundNumber() == 0) {
            return validateRoundZeroProposalHasNoRoundChangeCertificate(proposal);
        } else {
            return validateRoundChangeCertificateIsValidAndMatchesProposedBlock(proposal);
        }
    }

    private boolean validateRoundZeroProposalHasNoRoundChangeCertificate(final AgentProposal proposal) {
        if (proposal.getRoundChangeCertificate().isPresent()) {
            LOG.info(
                    "Illegal Proposal message, round-0 proposal must not contain a round change certificate.");
            return false;
        }
        return true;
    }

    private boolean validateRoundChangeCertificateIsValidAndMatchesProposedBlock(
            final AgentProposal proposal) {

        final Optional<AgentRoundChangeCertificate> roundChangeCertificate =
                proposal.getRoundChangeCertificate();

        if (!roundChangeCertificate.isPresent()) {
            LOG.info(
                    "Illegal Proposal message, rounds other than 0 must contain a round change certificate.");
            return false;
        }

        final AgentRoundChangeCertificate roundChangeCert = roundChangeCertificate.get();

        if (!roundChangeCertificateValidator.validateRoundChangeMessagesAndEnsureTargetRoundMatchesRoot(
                proposal.getRoundIdentifier(), roundChangeCert)) {
            LOG.info("Illegal Proposal message, embedded RoundChangeCertificate is not self-consistent");
            return false;
        }

        if (!roundChangeCertificateValidator.validateProposalMessageMatchesLatestPrepareCertificate(
                roundChangeCert, proposal.getBlock())) {
            LOG.info(
                    "Illegal Proposal message, piggybacked block does not match latest PrepareCertificate");
            return false;
        }

        return true;
    }

    public boolean validatePrepare(final AgentPrepare msg) {
        return signedDataValidator.validatePrepare(msg.getSignedPayload());
    }

    public boolean validateCommit(final AgentCommit msg) {
        return signedDataValidator.validateCommit(msg.getSignedPayload());
    }

}

