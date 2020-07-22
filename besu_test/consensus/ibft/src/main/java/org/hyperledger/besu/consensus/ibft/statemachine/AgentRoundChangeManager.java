package org.hyperledger.besu.consensus.ibft.statemachine;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.consensus.ibft.validation.AgentRoundChangeMessageValidator;
import org.hyperledger.besu.ethereum.core.Address;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class AgentRoundChangeManager {
    public static class AgentRoundChangeStatus {

        private final long quorum;

        @VisibleForTesting
        final Map<Address, AgentRoundChange> receivedMessages = Maps.newLinkedHashMap();

        private boolean actioned = false;

        public AgentRoundChangeStatus(final long quorum) {
            this.quorum = quorum;
        }

        public void addMessage(final AgentRoundChange msg) {
            if (!actioned) {
                receivedMessages.putIfAbsent(msg.getAuthor(), msg);
            }
        }

        public boolean roundChangeReady() {
            return receivedMessages.size() >= quorum && !actioned;
        }

        public Collection<AgentRoundChange> createRoundChangeCertificate() {
            if (roundChangeReady()) {
                actioned = true;
                return receivedMessages.values();
            } else {
                throw new IllegalStateException("Unable to create RoundChangeCertificate at this time.");
            }
        }
    }

    private static final Logger LOG = LogManager.getLogger();

    @VisibleForTesting
    final Map<ConsensusRoundIdentifier, AgentRoundChangeStatus> roundChangeCache = Maps.newHashMap();

    private final long quorum;
    private final AgentRoundChangeMessageValidator roundChangeMessageValidator;

    public AgentRoundChangeManager(
            final long quorum, final AgentRoundChangeMessageValidator roundChangeMessageValidator) {
        this.quorum = quorum;
        this.roundChangeMessageValidator = roundChangeMessageValidator;
    }


    public Optional<Collection<AgentRoundChange>> appendRoundChangeMessage(final AgentRoundChange msg) {

        if (!isMessageValid(msg)) {
            LOG.info("RoundChange message was invalid.");
            return Optional.empty();
        }

        final AgentRoundChangeStatus roundChangeStatus = storeRoundChangeMessage(msg);

        if (roundChangeStatus.roundChangeReady()) {
            return Optional.of(roundChangeStatus.createRoundChangeCertificate());
        }

        return Optional.empty();
    }

    private boolean isMessageValid(final AgentRoundChange msg) {
        return roundChangeMessageValidator.validateRoundChange(msg);
    }

    private AgentRoundChangeStatus storeRoundChangeMessage(final AgentRoundChange msg) {
        final ConsensusRoundIdentifier msgTargetRound = msg.getRoundIdentifier();

        final AgentRoundChangeStatus roundChangeStatus =
                roundChangeCache.computeIfAbsent(msgTargetRound, ignored -> new AgentRoundChangeStatus(quorum));

        roundChangeStatus.addMessage(msg);

        return roundChangeStatus;
    }

    public void discardRoundsPriorTo(final ConsensusRoundIdentifier completedRoundIdentifier) {
        roundChangeCache.keySet().removeIf(k -> isAnEarlierRound(k, completedRoundIdentifier));
    }

    private boolean isAnEarlierRound(
            final ConsensusRoundIdentifier left, final ConsensusRoundIdentifier right) {
        return left.getRoundNumber() < right.getRoundNumber();
    }
}
