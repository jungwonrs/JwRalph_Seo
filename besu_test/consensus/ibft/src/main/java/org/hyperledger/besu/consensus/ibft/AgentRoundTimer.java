package org.hyperledger.besu.consensus.ibft;

import org.hyperledger.besu.consensus.ibft.ibftevent.AgentRoundExpiry;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AgentRoundTimer {
    private final AgentExecutors agentExecutors;
    private Optional<ScheduledFuture<?>> currentTimerTask;
    private final AgentEventQueue queue;
    private final long baseExpiryMillis;

    public AgentRoundTimer(
            final AgentEventQueue queue, final long baseExpirySeconds, final AgentExecutors agentExecutors) {
        this.queue = queue;
        this.agentExecutors = agentExecutors;
        this.currentTimerTask = Optional.empty();
        this.baseExpiryMillis = baseExpirySeconds * 1000;
    }

    public synchronized void cancelTimer() {
        currentTimerTask.ifPresent(t -> t.cancel(false));
        currentTimerTask = Optional.empty();
    }

    public synchronized boolean isRunning() {
        return currentTimerTask.map(t -> !t.isDone()).orElse(false);
    }

    public synchronized void startTimer(final ConsensusRoundIdentifier round) {
        cancelTimer();

        final long expiryTime = baseExpiryMillis * (long) Math.pow(2, round.getRoundNumber());

        final Runnable newTimerRunnable = () -> queue.add(new AgentRoundExpiry(round));

    final ScheduledFuture<?> newTimerTask =
            agentExecutors.scheduleTask(newTimerRunnable, expiryTime, TimeUnit.MILLISECONDS);
    currentTimerTask =Optional.of(newTimerTask);
    }
}
