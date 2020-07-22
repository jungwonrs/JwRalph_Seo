package org.hyperledger.besu.consensus.ibft;

import static org.hyperledger.besu.ethereum.eth.manager.MonitoredExecutors.newScheduledThreadPool;

import org.hyperledger.besu.plugin.services.MetricsSystem;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class AgentExecutors {
    private enum State {
        IDLE,
        RUNNING,
        STOPPED
    }

    private static final Logger LOG = LogManager.getLogger();

    private final Duration shutdownTimeout = Duration.ofSeconds(30);
    private final MetricsSystem metricsSystem;

    private volatile ScheduledExecutorService timerExecutor;
    private volatile ExecutorService agentProcessorExecutor;
    private volatile State state = State.IDLE;

    private AgentExecutors(final MetricsSystem metricsSystem){this.metricsSystem = metricsSystem;}

    public static AgentExecutors create(final MetricsSystem metricsSystem) {return new AgentExecutors(metricsSystem);}

    public synchronized void start(){
        if (state != State.IDLE){
            return;
        }
        state = State.RUNNING;
        agentProcessorExecutor = Executors.newSingleThreadExecutor();
        timerExecutor = newScheduledThreadPool("AgentTimerExecutor", 1, metricsSystem);
    }

    public void stop(){
        synchronized (this){
            if(state != State.RUNNING){
                return;
            }
            state = State.STOPPED;
        }

        timerExecutor.shutdown();
        agentProcessorExecutor.shutdownNow();

    }

    public void awaitStop() throws InterruptedException {
        if (!timerExecutor.awaitTermination(shutdownTimeout.getSeconds(), TimeUnit.SECONDS)){
            LOG.error("{} timer executor did not shutdown cleanly.", getClass().getSimpleName());
        }
        if (!agentProcessorExecutor.awaitTermination(shutdownTimeout.getSeconds(), TimeUnit.SECONDS)){
            LOG.error("{} agentProcessor executor did not shutdown cleanly.", getClass().getSimpleName());
        }
    }

    public synchronized void executeAgentProcessor(final AgentProcessor agentProcessor){
        assertRunning();
        agentProcessorExecutor.execute(agentProcessor);
    }

    public synchronized ScheduledFuture<?> scheduleTask(
            final Runnable command, final long delay, final TimeUnit unit) {
        assertRunning();
        return timerExecutor.schedule(command, delay, unit);
    }

    private void assertRunning(){
        if(state != State.RUNNING){
            throw new IllegalStateException(
                    "Attempt to interact with"
                    + getClass().getSimpleName()
                    + "that is not running. Current State is"
                    + state.name()
                    + ".");
        }
    }

}
