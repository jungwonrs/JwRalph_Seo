package org.hyperledger.besu.consensus.ibft.statemachine;

import static org.hyperledger.besu.consensus.ibft.statemachine.AgentBlockHeightManager.MessageAge.CURRENT_ROUND;
import static org.hyperledger.besu.consensus.ibft.statemachine.AgentBlockHeightManager.MessageAge.FUTURE_ROUND;
import static org.hyperledger.besu.consensus.ibft.statemachine.AgentBlockHeightManager.MessageAge.PRIOR_ROUND;

import org.hyperledger.besu.consensus.ibft.AgentBlockTimer;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentRoundExpiry;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentMessage;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.consensus.ibft.network.AgentMessageTransmitter;
import org.hyperledger.besu.consensus.ibft.payload.AgentMessageFactory;
import org.hyperledger.besu.consensus.ibft.payload.AgentPayload;
import org.hyperledger.besu.consensus.ibft.validation.AgentFutureRoundProposalMessageValidator;
import org.hyperledger.besu.consensus.ibft.validation.AgentMessageValidatorFactory;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.plugin.services.securitymodule.SecurityModuleException;

import java.time.Clock;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AgentBlockHeightManager implements AgentBlockHeightManagers{
    private static final Logger LOG = LogManager.getLogger();

    private final AgentRoundFactory roundFactory;
    private final AgentRoundChangeManager roundChangeManager;
    private final BlockHeader parentHeader;
    private final AgentBlockTimer blockTimer;
    private final AgentMessageTransmitter transmitter;
    private final AgentMessageFactory messageFactory;
    private final Map<Integer, AgentRoundState> futureRoundStateBuffer = Maps.newHashMap();
    private final AgentFutureRoundProposalMessageValidator futureRoundProposalMessageValidator;
    private final Clock clock;
    private final Function<ConsensusRoundIdentifier, AgentRoundState> roundStateCreator;
    private final AgentFinalState finalState;

    private Optional<AgentPreparedRoundArtifacts> latestPreparedRoundArtifacts = Optional.empty();

    private AgentRound currentRound;

    public AgentBlockHeightManager(
            final BlockHeader parentHeader,
            final AgentFinalState finalState,
            final AgentRoundChangeManager roundChangeManager,
            final AgentRoundFactory agentRoundFactory,
            final Clock clock,
            final AgentMessageValidatorFactory messageValidatorFactory) {
        this.parentHeader = parentHeader;
        this.roundFactory = agentRoundFactory;
        this.blockTimer = finalState.getBlockTimer();
        this.transmitter = finalState.getTransmitter();
        this.messageFactory = finalState.getMessageFactory();
        this.clock = clock;
        this.roundChangeManager = roundChangeManager;
        this.finalState = finalState;

        futureRoundProposalMessageValidator =
                messageValidatorFactory.createFutureRoundProposalMessageValidator(
                        getChainHeight(), parentHeader);

        roundStateCreator =
                (roundIdentifier) ->
                        new AgentRoundState(
                                roundIdentifier,
                                finalState.getQuorum(),
                                messageValidatorFactory.createMessageValidator(roundIdentifier, parentHeader));

        currentRound = roundFactory.createNewRound(parentHeader, 0);
        if (finalState.isLocalNodeProposerForRound(currentRound.getRoundIdentifier())) {
            blockTimer.startTimer(currentRound.getRoundIdentifier(), parentHeader);
        }
    }

    @Override
    public void handleBlockTimerExpiry(final ConsensusRoundIdentifier roundIdentifier) {
        if (roundIdentifier.equals(currentRound.getRoundIdentifier())) {
            currentRound.createAndSendProposalMessage(clock.millis() / 1000);
        } else {
            LOG.trace(
                    "Block timer expired for a round ({}) other than current ({})",
                    roundIdentifier,
                    currentRound.getRoundIdentifier());
        }
    }

    @Override
    public void roundExpired(final AgentRoundExpiry expire) {
        if (!expire.getView().equals(currentRound.getRoundIdentifier())) {
            LOG.trace(
                    "Ignoring Round timer expired which does not match current round. round={}, timerRound={}",
                    currentRound.getRoundIdentifier(),
                    expire.getView());
            return;
        }

        LOG.debug(
                "Round has expired, creating PreparedCertificate and notifying peers. round={}",
                currentRound.getRoundIdentifier());
        final Optional<AgentPreparedRoundArtifacts> preparedRoundArtifacts =
                currentRound.constructPreparedRoundArtifacts();

        if (preparedRoundArtifacts.isPresent()) {
            latestPreparedRoundArtifacts = preparedRoundArtifacts;
        }

        startNewRound(currentRound.getRoundIdentifier().getRoundNumber() + 1);

        try {
            final AgentRoundChange localRoundChange =
                    messageFactory.createRoundChange(
                            currentRound.getRoundIdentifier(), latestPreparedRoundArtifacts);

            // Its possible the locally created RoundChange triggers the transmission of a NewRound
            // message - so it must be handled accordingly.
            handleRoundChangePayload(localRoundChange);
        } catch (final SecurityModuleException e) {
            LOG.warn("Failed to create signed RoundChange message.", e);
        }

        transmitter.multicastRoundChange(
                currentRound.getRoundIdentifier(), latestPreparedRoundArtifacts);
    }

    @Override
    public void handleProposalPayload(final AgentProposal proposal) {
        LOG.trace("Received a Proposal Payload.");
        final MessageAge messageAge =
                determineAgeOfPayload(proposal.getRoundIdentifier().getRoundNumber());

        if (messageAge == PRIOR_ROUND) {
            LOG.trace("Received Proposal Payload for a prior round={}", proposal.getRoundIdentifier());
        } else {
            if (messageAge == FUTURE_ROUND) {
                if (!futureRoundProposalMessageValidator.validateProposalMessage(proposal)) {
                    LOG.info("Received future Proposal which is illegal, no round change triggered.");
                    return;
                }
                startNewRound(proposal.getRoundIdentifier().getRoundNumber());
            }
            currentRound.handleProposalMessage(proposal);
        }
    }

    @Override
    public void handlePreparePayload(final AgentPrepare prepare) {
        LOG.trace("Received a Prepare Payload.");
        actionOrBufferMessage(
                prepare, currentRound::handlePrepareMessage, AgentRoundState::addPrepareMessage);
    }

    @Override
    public void handleCommitPayload(final AgentCommit commit) {
        LOG.trace("Received a Commit Payload.");
        actionOrBufferMessage(commit, currentRound::handleCommitMessage, AgentRoundState::addCommitMessage);
    }

    private <P extends AgentPayload, M extends AgentMessage<P>> void actionOrBufferMessage(
            final M agentMessage,
            final Consumer<M> inRoundHandler,
            final BiConsumer<AgentRoundState, M> buffer) {
        final MessageAge messageAge =
                determineAgeOfPayload(agentMessage.getRoundIdentifier().getRoundNumber());
        if (messageAge == CURRENT_ROUND) {
            inRoundHandler.accept(agentMessage);
        } else if (messageAge == FUTURE_ROUND) {
            final ConsensusRoundIdentifier msgRoundId = agentMessage.getRoundIdentifier();
            final AgentRoundState roundstate =
                    futureRoundStateBuffer.computeIfAbsent(
                            msgRoundId.getRoundNumber(), k -> roundStateCreator.apply(msgRoundId));
            buffer.accept(roundstate, agentMessage);
        }
    }

    @Override
    public void handleRoundChangePayload(final AgentRoundChange message) {
        final ConsensusRoundIdentifier targetRound = message.getRoundIdentifier();
        LOG.trace("Received a RoundChange Payload for {}", targetRound);

        final MessageAge messageAge =
                determineAgeOfPayload(message.getRoundIdentifier().getRoundNumber());
        if (messageAge == PRIOR_ROUND) {
            LOG.trace("Received RoundChange Payload for a prior round. targetRound={}", targetRound);
            return;
        }

        final Optional<Collection<AgentRoundChange>> result =
                roundChangeManager.appendRoundChangeMessage(message);
        if (result.isPresent()) {
            LOG.debug(
                    "Received sufficient RoundChange messages to change round to targetRound={}",
                    targetRound);
            if (messageAge == FUTURE_ROUND) {
                startNewRound(targetRound.getRoundNumber());
            }

            final AgentRoundChangeArtifacts roundChangeArtifacts = AgentRoundChangeArtifacts.create(result.get());

            if (finalState.isLocalNodeProposerForRound(targetRound)) {
                currentRound.startRoundWith(
                        roundChangeArtifacts, TimeUnit.MILLISECONDS.toSeconds(clock.millis()));
            }
        }
    }

    private void startNewRound(final int roundNumber) {
        LOG.debug("Starting new round {}", roundNumber);
        if (futureRoundStateBuffer.containsKey(roundNumber)) {
            currentRound =
                    roundFactory.createNewRoundWithState(
                            parentHeader, futureRoundStateBuffer.get(roundNumber));
            futureRoundStateBuffer.keySet().removeIf(k -> k <= roundNumber);
        } else {
            currentRound = roundFactory.createNewRound(parentHeader, roundNumber);
        }
        // discard roundChange messages from the current and previous rounds
        roundChangeManager.discardRoundsPriorTo(currentRound.getRoundIdentifier());
    }

    @Override
    public long getChainHeight() {
        return parentHeader.getNumber() + 1;
    }

    @Override
    public BlockHeader getParentBlockHeader() {
        return parentHeader;
    }

    private MessageAge determineAgeOfPayload(final int messageRoundNumber) {
        final int currentRoundNumber = currentRound.getRoundIdentifier().getRoundNumber();
        if (messageRoundNumber > currentRoundNumber) {
            return FUTURE_ROUND;
        } else if (messageRoundNumber == currentRoundNumber) {
            return CURRENT_ROUND;
        }
        return PRIOR_ROUND;
    }

    public enum MessageAge {
        PRIOR_ROUND,
        CURRENT_ROUND,
        FUTURE_ROUND
    }
}


