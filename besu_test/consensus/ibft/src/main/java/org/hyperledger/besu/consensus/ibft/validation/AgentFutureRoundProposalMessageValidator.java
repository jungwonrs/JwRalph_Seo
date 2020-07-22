package org.hyperledger.besu.consensus.ibft.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.ethereum.core.BlockHeader;

public class AgentFutureRoundProposalMessageValidator {

    private static final Logger LOG = LogManager.getLogger();

    private final AgentMessageValidatorFactory messageValidatorFactory;
    private final long chainHeight;
    private final BlockHeader parentHeader;

    public AgentFutureRoundProposalMessageValidator(
            final AgentMessageValidatorFactory messageValidatorFactory,
            final long chainHeight,
            final BlockHeader parentHeader) {
        this.messageValidatorFactory = messageValidatorFactory;
        this.chainHeight = chainHeight;
        this.parentHeader = parentHeader;
    }

    public boolean validateProposalMessage(final AgentProposal msg) {

        if (msg.getRoundIdentifier().getSequenceNumber() != chainHeight) {
            LOG.info("Illegal Proposal message, does not target the correct round height.");
            return false;
        }

        final AgentMessageValidator messageValidator =
                messageValidatorFactory.createMessageValidator(msg.getRoundIdentifier(), parentHeader);

        return messageValidator.validateProposal(msg);
    }
}