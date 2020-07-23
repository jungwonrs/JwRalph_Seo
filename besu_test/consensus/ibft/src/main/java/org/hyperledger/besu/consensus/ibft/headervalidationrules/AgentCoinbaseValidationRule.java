package org.hyperledger.besu.consensus.ibft.headervalidationrules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.common.ValidatorProvider;
import org.hyperledger.besu.consensus.ibft.IbftContext;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.mainnet.AttachedBlockHeaderValidationRule;

import java.util.Collection;

public class AgentCoinbaseValidationRule implements AttachedBlockHeaderValidationRule {
    private static final Logger LOGGER = LogManager.getLogger(IbftCoinbaseValidationRule.class);

    @Override
    public boolean validate(
            final BlockHeader header, final BlockHeader parent, final ProtocolContext context) {

        final ValidatorProvider validatorProvider =
                context
                        .getConsensusState(IbftContext.class)
                        .getVoteTallyCache()
                        .getVoteTallyAfterBlock(parent);
        final Address proposer = header.getCoinbase();

        final Collection<Address> storedValidators = validatorProvider.getValidators();

        if (!storedValidators.contains(proposer)) {
            LOGGER.trace(
                    "Block proposer is not a member of the validators. proposer={}, validators={}",
                    proposer,
                    storedValidators);
            return false;
        }

        return true;
    }
}
