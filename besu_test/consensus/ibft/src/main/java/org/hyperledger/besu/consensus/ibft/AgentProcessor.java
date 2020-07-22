package org.hyperledger.besu.consensus.ibft;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentEvent;


public class AgentProcessor implements Runnable {

    private static final Logger LOG = LogManager.getLogger();

    private final AgentEventQueue incomingQueue;
    private volatile boolean shutdown = false;
    private final AgentEventMultiplexer eventMultiplexer;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public AgentProcessor(
            final AgentEventQueue incomingQueue, final AgentEventMultiplexer eventMultiplexer){
        this.incomingQueue = incomingQueue;
        this.eventMultiplexer = eventMultiplexer;
    }

    public void stop() {shutdown = true;}

    public void awaitStop() throws InterruptedException {
        shutdownLatch.await();
    }

    @Override
    public void run (){
        try{
            while (!shutdown){
                nextAgentEvent().ifPresent(eventMultiplexer::handleAgentEvent);
            }
        } catch (final Throwable t){
            LOG.error("Agent Mining thread has suffered a fatal error, mining has been halted", t);
        }
        LOG.info("Shutting down Agent event processor");
        shutdownLatch.countDown();
    }

    private Optional<AgentEvent> nextAgentEvent(){
        try{
            return Optional.ofNullable(incomingQueue.poll(500, TimeUnit.MILLISECONDS));
        } catch (final InterruptedException interrupt){
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

}
