package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.AgentHelper;
import org.hyperledger.besu.consensus.ibft.validation.AgentMessageValidatorFactory;
import org.hyperledger.besu.ethereum.core.BlockHeader;

public class AgentBlockHeightManagerFactory {

    private final AgentRoundFactory roundFactory;
    private final AgentFinalState finalState;
    private final AgentMessageValidatorFactory messageValidatorFactory;

    public AgentBlockHeightManagerFactory(
            final AgentFinalState finalState,
            final AgentRoundFactory roundFactory,
            final AgentMessageValidatorFactory messageValidatorFactory) {
        this.roundFactory = roundFactory;
        this.finalState = finalState;
        this.messageValidatorFactory = messageValidatorFactory;
    }

    public AgentBlockHeightManagers create(final BlockHeader parentHeader) {
        if (finalState.isLocalNodeValidator()) {
            return createFullBlockHeightManager(parentHeader);
        } else {
            return createNoOpBlockHeightManager(parentHeader);
        }
    }

    private AgentBlockHeightManagers createNoOpBlockHeightManager(final BlockHeader parentHeader) {
        return new AgentNoOpBlockHeightManagers(parentHeader);
    }

    private AgentBlockHeightManagers createFullBlockHeightManager(final BlockHeader parentHeader) {
        return new AgentBlockHeightManager(
                parentHeader,
                finalState,
                new AgentRoundChangeManager(
                        AgentHelper.calculateRequiredValidatorQuorum(finalState.getValidators().size()),
                        messageValidatorFactory.createRoundChangeMessageValidator(
                                parentHeader.getNumber() + 1L, parentHeader)),
                roundFactory,
                finalState.getClock(),
                messageValidatorFactory);
    }
}

