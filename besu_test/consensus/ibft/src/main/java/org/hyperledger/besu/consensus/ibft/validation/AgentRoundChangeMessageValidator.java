package org.hyperledger.besu.consensus.ibft.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;

public class AgentRoundChangeMessageValidator {
    private static final Logger LOG = LogManager.getLogger();

    private final AgentRoundChangePayloadValidator roundChangePayloadValidator;
    private final AgentProposalBlockConsistencyValidator proposalBlockConsistencyValidator;

    public AgentRoundChangeMessageValidator(
            final AgentRoundChangePayloadValidator roundChangePayloadValidator,
            final AgentProposalBlockConsistencyValidator proposalBlockConsistencyValidator) {
        this.proposalBlockConsistencyValidator = proposalBlockConsistencyValidator;
        this.roundChangePayloadValidator = roundChangePayloadValidator;
    }

    public boolean validateRoundChange(final AgentRoundChange msg) {

        if (!roundChangePayloadValidator.validateRoundChange(msg.getSignedPayload())) {
            LOG.info("Invalid RoundChange message, signed data did not validate correctly.");
            return false;
        }

        if (msg.getPreparedCertificate().isPresent() != msg.getProposedBlock().isPresent()) {
            LOG.info(
                    "Invalid RoundChange message, availability of certificate does not correlate with availability of block.");
            return false;
        }

        if (msg.getPreparedCertificate().isPresent()) {
            if (!proposalBlockConsistencyValidator.validateProposalMatchesBlock(
                    msg.getPreparedCertificate().get().getProposalPayload(), msg.getProposedBlock().get())) {
                LOG.info("Invalid RoundChange message, proposal did not align with supplied block.");
                return false;
            }
        }

        return true;
    }


}
