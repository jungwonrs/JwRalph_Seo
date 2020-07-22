package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentRoundExpiry;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.ethereum.core.BlockHeader;

public class AgentNoOpBlockHeightManagers implements AgentBlockHeightManagers {


    private final BlockHeader parentHeader;

    public AgentNoOpBlockHeightManagers(final BlockHeader parentHeader) {
        this.parentHeader = parentHeader;
    }

    @Override
    public void handleBlockTimerExpiry(final ConsensusRoundIdentifier roundIdentifier) {}

    @Override
    public void roundExpired(final AgentRoundExpiry expire) {}

    @Override
    public void handleProposalPayload(final AgentProposal proposal) {}

    @Override
    public void handlePreparePayload(final AgentPrepare prepare) {}

    @Override
    public void handleCommitPayload(final AgentCommit commit) {}

    @Override
    public void handleRoundChangePayload(final AgentRoundChange roundChange) {}

    @Override
    public long getChainHeight() {
        return parentHeader.getNumber() + 1;
    }

    @Override
    public BlockHeader getParentBlockHeader() {
        return parentHeader;
    }
}


