package org.hyperledger.besu.consensus.common;

import static com.google.common.base.Preconditions.checkNotNull;

import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.core.BlockHeader;

public class AgentForkingVoteTallyCache extends VoteTallyCache {

    private final AgentValidatorOverrides validatorOverrides;


    public AgentForkingVoteTallyCache(
            final Blockchain blockchain,
            final VoteTallyUpdater voteTallyUpdater,
            final EpochManager epochManager,
            final BlockInterface blockInterface,
            final AgentValidatorOverrides validatorOverrides
    ){
        super(blockchain, voteTallyUpdater, epochManager, blockInterface);
        checkNotNull(validatorOverrides);
        this.validatorOverrides = validatorOverrides;
    }

    @Override
    protected  VoteTally getValidatorsAfter(final BlockHeader header){
        final long nextBlockNumber = header.getNumber() + 1L;

        return validatorOverrides
                .getForBlock(nextBlockNumber)
                .map(VoteTally::new)
                .orElse(super.getValidatorsAfter(header));
    }

}
