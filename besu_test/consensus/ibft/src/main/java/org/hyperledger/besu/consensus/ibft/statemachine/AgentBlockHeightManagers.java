package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentRoundExpiry;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.ethereum.core.BlockHeader;

public interface AgentBlockHeightManagers {

    void handleBlockTimerExpiry(ConsensusRoundIdentifier roundIdentifier);

    void roundExpired(AgentRoundExpiry expire);

    void handleProposalPayload(AgentProposal proposal);

    void handlePreparePayload(AgentPrepare prepare);

    void handleCommitPayload(AgentCommit commit);

    void handleRoundChangePayload(AgentRoundChange roundChange);

    long getChainHeight();

    BlockHeader getParentBlockHeader();
}
