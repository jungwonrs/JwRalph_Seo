package org.hyperledger.besu.consensus.ibft.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.payload.AgentPreparePayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentPreparedCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentProposalPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangePayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.ethereum.core.Address;

import java.util.Collection;

public class AgentRoundChangePayloadValidator {
    private static final Logger LOG = LogManager.getLogger();

    private final MessageValidatorForHeightFactory messageValidatorFactory;
    private final Collection<Address> validators;
    private final long minimumPrepareMessages;
    private final long chainHeight;

    public AgentRoundChangePayloadValidator(
            final MessageValidatorForHeightFactory messageValidatorFactory,
            final Collection<Address> validators,
            final long minimumPrepareMessages,
            final long chainHeight) {
        this.messageValidatorFactory = messageValidatorFactory;
        this.validators = validators;
        this.minimumPrepareMessages = minimumPrepareMessages;
        this.chainHeight = chainHeight;
    }

    public boolean validateRoundChange(final AgentSignedData<AgentRoundChangePayload> msg) {

        if (!validators.contains(msg.getAuthor())) {
            LOG.info(
                    "Invalid RoundChange message, was not transmitted by a validator for the associated"
                            + " round.");
            return false;
        }

        final ConsensusRoundIdentifier targetRound = msg.getPayload().getRoundIdentifier();

        if (targetRound.getSequenceNumber() != chainHeight) {
            LOG.info("Invalid RoundChange message, not valid for local chain height.");
            return false;
        }

        if (msg.getPayload().getPreparedCertificate().isPresent()) {
            final AgentPreparedCertificate certificate = msg.getPayload().getPreparedCertificate().get();

            return validatePrepareCertificate(certificate, targetRound);
        }

        return true;
    }

    private boolean validatePrepareCertificate(
            final AgentPreparedCertificate certificate, final ConsensusRoundIdentifier roundChangeTarget) {
        final AgentSignedData<AgentProposalPayload> proposalMessage = certificate.getProposalPayload();

        final ConsensusRoundIdentifier proposalRoundIdentifier =
                proposalMessage.getPayload().getRoundIdentifier();

        if (!validatePreparedCertificateRound(proposalRoundIdentifier, roundChangeTarget)) {
            return false;
        }

        final AgentSignedDataValidator signedDataValidator =
                messageValidatorFactory.createAt(proposalRoundIdentifier);
        return validateConsistencyOfPrepareCertificateMessages(certificate, signedDataValidator);
    }

    private boolean validateConsistencyOfPrepareCertificateMessages(
            final AgentPreparedCertificate certificate, final AgentSignedDataValidator signedDataValidator) {

        if (!signedDataValidator.validateProposal(certificate.getProposalPayload())) {
            LOG.info("Invalid RoundChange message, embedded Proposal message failed validation.");
            return false;
        }

        if (certificate.getPreparePayloads().size() < minimumPrepareMessages) {
            LOG.info(
                    "Invalid RoundChange message, insufficient Prepare messages exist to justify "
                            + "prepare certificate.");
            return false;
        }

        for (final AgentSignedData<AgentPreparePayload> prepareMsg : certificate.getPreparePayloads()) {
            if (!signedDataValidator.validatePrepare(prepareMsg)) {
                LOG.info("Invalid RoundChange message, embedded Prepare message failed validation.");
                return false;
            }
        }

        return true;
    }

    private boolean validatePreparedCertificateRound(
            final ConsensusRoundIdentifier prepareCertRound,
            final ConsensusRoundIdentifier roundChangeTarget) {

        if (prepareCertRound.getSequenceNumber() != roundChangeTarget.getSequenceNumber()) {
            LOG.info("Invalid RoundChange message, PreparedCertificate is not for local chain height.");
            return false;
        }

        if (prepareCertRound.getRoundNumber() >= roundChangeTarget.getRoundNumber()) {
            LOG.info(
                    "Invalid RoundChange message, PreparedCertificate not older than RoundChange target.");
            return false;
        }
        return true;
    }

    @FunctionalInterface
    public interface MessageValidatorForHeightFactory {
        AgentSignedDataValidator createAt(final ConsensusRoundIdentifier roundIdentifier);
    }
}


