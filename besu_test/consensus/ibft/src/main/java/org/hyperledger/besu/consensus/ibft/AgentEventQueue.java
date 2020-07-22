package org.hyperledger.besu.consensus.ibft;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentEvent;

public class AgentEventQueue {
    private final BlockingQueue<AgentEvent> queue = new LinkedBlockingDeque<>();

    private static final Logger LOG = LogManager.getLogger();
    private final int messageQueueLimit;

    public AgentEventQueue(final int messageQueueLimit){
        this.messageQueueLimit = messageQueueLimit;
    }

    public void add (final AgentEvent event){
        if (queue.size() > messageQueueLimit){
            LOG.warn("Queue size exceeded trying to add new agent event {}", event);
        } else {
            queue.add(event);
        }
    }

    public int size(){ return queue.size();}

    public boolean isEmpty() {return queue.isEmpty();}

    @Nullable
    public AgentEvent poll(final long timeout, final TimeUnit unit) throws InterruptedException{
        return queue.poll(timeout, unit);
    }


}
