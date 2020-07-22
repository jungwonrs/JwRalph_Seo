package org.hyperledger.besu.consensus.ibft;

import org.hyperledger.besu.consensus.common.BlockInterface;
import org.hyperledger.besu.consensus.common.EpochManager;
import org.hyperledger.besu.consensus.common.PoaContext;
import org.hyperledger.besu.consensus.common.VoteProposer;
import org.hyperledger.besu.consensus.common.VoteTallyCache;

public class AgentContext implements PoaContext {

        private final VoteTallyCache voteTallyCache;
        private final VoteProposer voteProposer;
        private final EpochManager epochManager;
        private final BlockInterface blockInterface;

        public AgentContext(
                final VoteTallyCache voteTallyCache,
                final VoteProposer voteProposer,
                final EpochManager epochManager,
                final BlockInterface blockInterface) {
            this.voteTallyCache = voteTallyCache;
            this.voteProposer = voteProposer;
            this.epochManager = epochManager;
            this.blockInterface = blockInterface;
        }

        public VoteTallyCache getVoteTallyCache() {
            return voteTallyCache;
        }

        public VoteProposer getVoteProposer() {
            return voteProposer;
        }

        public EpochManager getEpochManager() {
            return epochManager;
        }

        @Override
        public BlockInterface getBlockInterface() {
            return blockInterface;
        }
}

