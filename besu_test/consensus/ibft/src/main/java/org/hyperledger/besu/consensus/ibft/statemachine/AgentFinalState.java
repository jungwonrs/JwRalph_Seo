package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.common.VoteTallyCache;
import org.hyperledger.besu.consensus.ibft.AgentBlockTimer;
import org.hyperledger.besu.consensus.ibft.AgentHelper;
import org.hyperledger.besu.consensus.ibft.AgentRoundTimer;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentBlockCreatorFactory;
import org.hyperledger.besu.consensus.ibft.blockcreation.ProposerSelector;
import org.hyperledger.besu.consensus.ibft.network.AgentMessageTransmitter;
import org.hyperledger.besu.consensus.ibft.network.ValidatorMulticaster;
import org.hyperledger.besu.consensus.ibft.payload.AgentMessageFactory;
import org.hyperledger.besu.crypto.NodeKey;
import org.hyperledger.besu.ethereum.core.Address;

import java.time.Clock;
import java.util.Collection;

public class AgentFinalState {
    private final VoteTallyCache voteTallyCache;
    private final NodeKey nodeKey;
    private final Address localAddress;
    private final ProposerSelector proposerSelector;
    private final AgentRoundTimer roundTimer;
    private final AgentBlockTimer blockTimer;
    private final AgentBlockCreatorFactory blockCreatorFactory;
    private final AgentMessageFactory messageFactory;
    private final AgentMessageTransmitter messageTransmitter;
    private final Clock clock;


    public AgentFinalState(
            final VoteTallyCache voteTallyCache,
            final NodeKey nodeKey,
            final Address localAddress,
            final ProposerSelector proposerSelector,
            final ValidatorMulticaster validatorMulticaster,
            final AgentRoundTimer roundTimer,
            final AgentBlockTimer blockTimer,
            final AgentBlockCreatorFactory blockCreatorFactory,
            final AgentMessageFactory messageFactory,
            final Clock clock
    ){
        this.voteTallyCache = voteTallyCache;
        this.nodeKey = nodeKey;
        this.localAddress = localAddress;
        this.proposerSelector = proposerSelector;
        this.roundTimer = roundTimer;
        this.blockTimer = blockTimer;
        this.blockCreatorFactory = blockCreatorFactory;
        this.messageFactory = messageFactory;
        this.clock = clock;
        this.messageTransmitter = new AgentMessageTransmitter(messageFactory, validatorMulticaster);
    }

    public int getQuorum() {return AgentHelper.calculateRequiredValidatorQuorum(getValidators().size());}

    public Collection<Address> getValidators() {return voteTallyCache.getVoteTallyAtHead().getValidators();}

    public NodeKey getNodeKey() {return  nodeKey;}

    public Address getLocalAddress() {return localAddress;}

    public boolean isLocalNodeProposerForRound(final ConsensusRoundIdentifier roundIdentifier){
        return getProposerForRound(roundIdentifier).equals(localAddress);
    }

    public boolean isLocalNodeValidator() {return getValidators().contains(localAddress);}

    public AgentRoundTimer getRoundTimer() {return  roundTimer;}

    public AgentBlockTimer getBlockTimer() {return  blockTimer;}

    public AgentBlockCreatorFactory getBlockCreatorFactory() {
        return blockCreatorFactory;
    }

    public AgentMessageFactory getMessageFactory() {
        return messageFactory;
    }

    public Address getProposerForRound(final ConsensusRoundIdentifier roundIdentifier){
        return proposerSelector.selectProposerForRound(roundIdentifier);
    }

    public AgentMessageTransmitter getTransmitter() {
        return messageTransmitter;
    }
    public Clock getClock() {return clock;}
}
