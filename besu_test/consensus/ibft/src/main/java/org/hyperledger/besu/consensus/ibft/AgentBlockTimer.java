package org.hyperledger.besu.consensus.ibft;

import org.bouncycastle.math.ec.ScaleYNegateXPointMap;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentBlockTimerExpiry;
import org.hyperledger.besu.consensus.ibft.ibftevent.BlockTimerExpiry;
import org.hyperledger.besu.ethereum.core.BlockHeader;

import java.time.Clock;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AgentBlockTimer {
    private final AgentExecutors agentExecutors;
    private Optional<ScheduledFuture<?>> currentTimerTask;
    private final AgentEventQueue queue;
    private final long minimumTimeBetweenBlocksMillis;
    private final Clock clock;


    public AgentBlockTimer(
            final AgentEventQueue queue,
            final long minimumTimeBetweenBlocksSeconds,
            final AgentExecutors agentExecutors,
            final Clock clock
    ){
        this.queue = queue;
        this.agentExecutors = agentExecutors;
        this.currentTimerTask = Optional.empty();
        this.minimumTimeBetweenBlocksMillis = minimumTimeBetweenBlocksSeconds * 1000;
        this.clock = clock;
    }


    public synchronized void cancelTimer(){
        currentTimerTask.ifPresent(t->t.cancel(false));
        currentTimerTask = Optional.empty();
    }

    public synchronized boolean isRunning() {return currentTimerTask.map(t -> !t.isDone()).orElse(false);}

    public synchronized void startTimer(
            final ConsensusRoundIdentifier round, final BlockHeader chainHeader
    ){
        cancelTimer();

        final long now = clock.millis();

        final long expiryTime = chainHeader.getTimestamp() * 1_000 + minimumTimeBetweenBlocksMillis;

        if (expiryTime > now){
            final long delay = expiryTime - now;

            final Runnable newTimerRunnable = () -> queue.add(new AgentBlockTimerExpiry(round));

            final ScheduledFuture<?> newTimerTask =
                    agentExecutors.scheduleTask(newTimerRunnable, delay, TimeUnit.MILLISECONDS);
            currentTimerTask = Optional.of(newTimerTask);
        } else {
            queue.add(new AgentBlockTimerExpiry(round));
        }
    }

}
