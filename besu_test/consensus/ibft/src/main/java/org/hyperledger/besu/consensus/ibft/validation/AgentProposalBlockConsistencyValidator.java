package org.hyperledger.besu.consensus.ibft.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.AgentExtraData;
import org.hyperledger.besu.consensus.ibft.payload.AgentProposalPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.ethereum.core.Block;

public class AgentProposalBlockConsistencyValidator {

    private static final Logger LOG = LogManager.getLogger();

    public boolean validateProposalMatchesBlock(
            final AgentSignedData<AgentProposalPayload> signedPayload, final Block proposedBlock) {

        if (!signedPayload.getPayload().getDigest().equals(proposedBlock.getHash())) {
            LOG.info("Invalid Proposal, embedded digest does not match block's hash.");
            return false;
        }

        if (proposedBlock.getHeader().getNumber()
                != signedPayload.getPayload().getRoundIdentifier().getSequenceNumber()) {
            LOG.info("Invalid proposal/block - message sequence does not align with block number.");
            return false;
        }

        if (!validateBlockMatchesProposalRound(signedPayload.getPayload(), proposedBlock)) {
            return false;
        }

        return true;
    }

    private boolean validateBlockMatchesProposalRound(
            final AgentProposalPayload payload, final Block block) {
        final ConsensusRoundIdentifier msgRound = payload.getRoundIdentifier();
        final AgentExtraData extraData = AgentExtraData.decode(block.getHeader());
        if (extraData.getRound() != msgRound.getRoundNumber()) {
            LOG.info("Invalid Proposal message, round number in block does not match that in message.");
            return false;
        }
        return true;
    }


}
