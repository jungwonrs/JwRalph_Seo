package org.hyperledger.besu.consensus.common;

import org.hyperledger.besu.ethereum.core.Address;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AgentValidatorOverrides {

    private final Map<Long, List<Address>> overriddenValidators;

    public AgentValidatorOverrides(final Map<Long, List<Address>> overriddenValidators){
        this.overriddenValidators = overriddenValidators;
    }

    public Optional<Collection<Address>> getForBlock(final long blockNumber){
        return Optional.ofNullable(overriddenValidators.get(blockNumber));
    }

}
