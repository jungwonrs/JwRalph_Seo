package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentBlockCreator;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentBlockCreatorFactory;
import org.hyperledger.besu.consensus.ibft.validation.AgentMessageValidatorFactory;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.chain.MinedBlockObserver;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;
import org.hyperledger.besu.util.Subscribers;

public class AgentRoundFactory {
    private final AgentFinalState finalState;
    private final AgentBlockCreatorFactory blockCreatorFactory;
    private final ProtocolContext protocolContext;
    private final ProtocolSchedule protocolSchedule;
    private final Subscribers<MinedBlockObserver> minedBlockObservers;
    private final AgentMessageValidatorFactory messageValidatorFactory;

    public AgentRoundFactory(
            final AgentFinalState finalState,
            final ProtocolContext protocolContext,
            final ProtocolSchedule protocolSchedule,
            final Subscribers<MinedBlockObserver> minedBlockObservers,
            final AgentMessageValidatorFactory messageValidatorFactory) {
        this.finalState = finalState;
        this.blockCreatorFactory = finalState.getBlockCreatorFactory();
        this.protocolContext = protocolContext;
        this.protocolSchedule = protocolSchedule;
        this.minedBlockObservers = minedBlockObservers;
        this.messageValidatorFactory = messageValidatorFactory;
    }

    public AgentRound createNewRound(final BlockHeader parentHeader, final int round) {
        long nextBlockHeight = parentHeader.getNumber() + 1;
        final ConsensusRoundIdentifier roundIdentifier =
                new ConsensusRoundIdentifier(nextBlockHeight, round);

        final AgentRoundState roundState =
                new AgentRoundState(
                        roundIdentifier,
                        finalState.getQuorum(),
                        messageValidatorFactory.createMessageValidator(roundIdentifier, parentHeader));

        return createNewRoundWithState(parentHeader, roundState);
    }

    public AgentRound createNewRoundWithState(
            final BlockHeader parentHeader, final AgentRoundState roundState) {
        final ConsensusRoundIdentifier roundIdentifier = roundState.getRoundIdentifier();
        final AgentBlockCreator blockCreator =
                blockCreatorFactory.create(parentHeader, roundIdentifier.getRoundNumber());

        return new AgentRound(
                roundState,
                blockCreator,
                protocolContext,
                protocolSchedule.getByBlockNumber(roundIdentifier.getSequenceNumber()).getBlockImporter(),
                minedBlockObservers,
                finalState.getNodeKey(),
                finalState.getMessageFactory(),
                finalState.getTransmitter(),
                finalState.getRoundTimer());
    }
}