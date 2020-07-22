package org.hyperledger.besu.consensus.ibft.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.AgentHelper;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.AgentBlockHeaderFunctions;
import org.hyperledger.besu.consensus.ibft.AgentBlockInterface;
import org.hyperledger.besu.consensus.ibft.payload.AgentPreparedCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangeCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangePayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.Block;

import java.util.Collection;
import java.util.Optional;

public class AgentRoundChangeCertificateValidator {
    private static final Logger LOG = LogManager.getLogger();

    private final Collection<Address> validators;
    private final AgentRoundChangePayloadValidator.MessageValidatorForHeightFactory messageValidatorFactory;
    private final long quorum;
    private final long chainHeight;

    public AgentRoundChangeCertificateValidator(
            final Collection<Address> validators,
            final AgentRoundChangePayloadValidator.MessageValidatorForHeightFactory messageValidatorFactory,
            final long chainHeight) {
        this.validators = validators;
        this.messageValidatorFactory = messageValidatorFactory;
        this.quorum = AgentHelper.calculateRequiredValidatorQuorum(validators.size());
        this.chainHeight = chainHeight;
    }

    public boolean validateRoundChangeMessagesAndEnsureTargetRoundMatchesRoot(
            final ConsensusRoundIdentifier expectedRound, final AgentRoundChangeCertificate roundChangeCert) {

        final Collection<AgentSignedData<AgentRoundChangePayload>> roundChangeMsgs =
                roundChangeCert.getRoundChangePayloads();

        if (hasDuplicateAuthors(roundChangeMsgs)) {
            return false;
        }

        if (roundChangeMsgs.size() < quorum) {
            LOG.info("Invalid RoundChangeCertificate, insufficient RoundChange messages.");
            return false;
        }

        if (!roundChangeCert.getRoundChangePayloads().stream()
                .allMatch(p -> p.getPayload().getRoundIdentifier().equals(expectedRound))) {
            LOG.info(
                    "Invalid RoundChangeCertificate, not all embedded RoundChange messages have a "
                            + "matching target round.");
            return false;
        }

        final AgentRoundChangePayloadValidator roundChangeValidator =
                new AgentRoundChangePayloadValidator(
                        messageValidatorFactory,
                        validators,
                        AgentHelper.prepareMessageCountForQuorum(quorum),
                        chainHeight);

        if (!roundChangeCert.getRoundChangePayloads().stream()
                .allMatch(roundChangeValidator::validateRoundChange)) {
            LOG.info("Invalid RoundChangeCertificate, embedded RoundChange message failed validation.");
            return false;
        }

        return true;
    }

    private boolean hasDuplicateAuthors(
            final Collection<AgentSignedData<AgentRoundChangePayload>> roundChangeMsgs) {
        final long distinctAuthorCount =
                roundChangeMsgs.stream().map(AgentSignedData::getAuthor).distinct().count();

        if (distinctAuthorCount != roundChangeMsgs.size()) {
            LOG.info("Invalid RoundChangeCertificate, multiple RoundChanges from the same author.");
            return true;
        }
        return false;
    }

    public boolean validateProposalMessageMatchesLatestPrepareCertificate(
            final AgentRoundChangeCertificate roundChangeCert, final Block proposedBlock) {

        final Collection<AgentSignedData<AgentRoundChangePayload>> roundChangePayloads =
                roundChangeCert.getRoundChangePayloads();

        final Optional<AgentPreparedCertificate> latestPreparedCertificate =
                AgentHelper.findLatestPreparedCertificate(roundChangePayloads);

        if (!latestPreparedCertificate.isPresent()) {
            LOG.debug(
                    "No round change messages have a preparedCertificate, any valid block may be proposed.");
            return true;
        }

        // Need to check that if we substitute the LatestPrepareCert round number into the supplied
        // block that we get the SAME hash as PreparedCert.
        final Block currentBlockWithOldRound =
                AgentBlockInterface.replaceRoundInBlock(
                        proposedBlock,
                        latestPreparedCertificate
                                .get()
                                .getProposalPayload()
                                .getPayload()
                                .getRoundIdentifier()
                                .getRoundNumber(),
                        AgentBlockHeaderFunctions.forCommittedSeal());

        if (!currentBlockWithOldRound
                .getHash()
                .equals(latestPreparedCertificate.get().getProposalPayload().getPayload().getDigest())) {
            LOG.info(
                    "Invalid RoundChangeCertificate, block in latest RoundChange does not match proposed block.");
            return false;
        }

        return true;
    }
}

