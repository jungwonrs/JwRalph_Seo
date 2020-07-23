package org.hyperledger.besu.consensus.ibft.headervalidationrules;

import org.apache.logging.log4j.LogManager;
import org.hyperledger.besu.consensus.common.ValidatorProvider;
import org.hyperledger.besu.consensus.ibft.AgentContext;
import org.hyperledger.besu.consensus.ibft.AgentExtraData;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.mainnet.AttachedBlockHeaderValidationRule;
import com.google.common.collect.Iterables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.ethereum.rlp.RLPException;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeSet;


public class AgentValidatorsValidationRule implements AttachedBlockHeaderValidationRule {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public boolean validate(
            final BlockHeader header, final BlockHeader parent, final ProtocolContext context) {
        try {
            final ValidatorProvider validatorProvider =
                    context
                            .getConsensusState(AgentContext.class)
                            .getVoteTallyCache()
                            .getVoteTallyAfterBlock(parent);
            final AgentExtraData agentExtraData = AgentExtraData.decode(header);

            final NavigableSet<Address> sortedReportedValidators =
                    new TreeSet<>(agentExtraData.getValidators());

            if (!Iterables.elementsEqual(agentExtraData.getValidators(), sortedReportedValidators)) {
                LOGGER.trace(
                        "Validators are not sorted in ascending order. Expected {} but got {}.",
                        sortedReportedValidators,
                        agentExtraData.getValidators());
                return false;
            }

            final Collection<Address> storedValidators = validatorProvider.getValidators();
            if (!Iterables.elementsEqual(agentExtraData.getValidators(), storedValidators)) {
                LOGGER.trace(
                        "Incorrect validators. Expected {} but got {}.",
                        storedValidators,
                        agentExtraData.getValidators());
                return false;
            }

        } catch (final RLPException ex) {
            LOGGER.trace("ExtraData field was unable to be deserialised into an AGENT Struct.", ex);
            return false;
        } catch (final IllegalArgumentException ex) {
            LOGGER.trace("Failed to verify extra data", ex);
            return false;
        }

        return true;
    }
}
