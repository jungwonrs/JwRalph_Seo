package org.hyperledger.besu.consensus.ibft.headervalidationrules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.common.ValidatorProvider;
import org.hyperledger.besu.consensus.ibft.AgentBlockHashing;
import org.hyperledger.besu.consensus.ibft.AgentContext;
import org.hyperledger.besu.consensus.ibft.AgentExtraData;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.mainnet.AttachedBlockHeaderValidationRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.hyperledger.besu.consensus.ibft.IbftHelpers.calculateRequiredValidatorQuorum;

public class AgentCommitSealsValidationRule implements AttachedBlockHeaderValidationRule {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public boolean validate(
            final BlockHeader header, final BlockHeader parent, final ProtocolContext protocolContext) {
        final ValidatorProvider validatorProvider =
                protocolContext
                        .getConsensusState(AgentContext.class)
                        .getVoteTallyCache()
                        .getVoteTallyAfterBlock(parent);
        final AgentExtraData ibftExtraData = AgentExtraData.decode(header);

        final List<Address> committers =
                AgentBlockHashing.recoverCommitterAddresses(header, ibftExtraData);
        final List<Address> committersWithoutDuplicates = new ArrayList<>(new HashSet<>(committers));

        if (committers.size() != committersWithoutDuplicates.size()) {
            LOGGER.trace("Duplicated seals found in header.");
            return false;
        }

        return validateCommitters(committersWithoutDuplicates, validatorProvider.getValidators());
    }

    private boolean validateCommitters(
            final Collection<Address> committers, final Collection<Address> storedValidators) {

        final int minimumSealsRequired = calculateRequiredValidatorQuorum(storedValidators.size());
        if (committers.size() < minimumSealsRequired) {
            LOGGER.trace(
                    "Insufficient committers to seal block. (Required {}, received {})",
                    minimumSealsRequired,
                    committers.size());
            return false;
        }

        if (!storedValidators.containsAll(committers)) {
            LOGGER.trace(
                    "Not all committers are in the locally maintained validator list. validators={} committers={}",
                    storedValidators,
                    committers);
            return false;
        }

        return true;
    }

    @Override
    public boolean includeInLightValidation() {
        return false;
    }
}

