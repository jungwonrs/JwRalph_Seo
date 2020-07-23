package org.hyperledger.besu.consensus.ibft.blockcreation;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.hyperledger.besu.consensus.ibft.AgentEventQueue;
import org.hyperledger.besu.consensus.ibft.AgentExecutors;
import org.hyperledger.besu.consensus.ibft.AgentProcessor;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentNewChainHead;
import org.hyperledger.besu.consensus.ibft.ibftevent.NewChainHead;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentController;
import org.hyperledger.besu.ethereum.blockcreation.MiningCoordinator;
import org.hyperledger.besu.ethereum.chain.BlockAddedEvent;
import org.hyperledger.besu.ethereum.chain.BlockAddedObserver;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.Transaction;
import org.hyperledger.besu.ethereum.core.Wei;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.Logger;
import org.apache.tuweni.bytes.Bytes;


public class AgentMiningCoordinator implements MiningCoordinator, BlockAddedObserver {

    private enum State {
        IDLE,
        RUNNING,
        STOPPED
    }

    private static final Logger LOG = getLogger();

    private final AgentController controller;
    private final AgentProcessor agentProcessor;
    private final AgentBlockCreatorFactory blockCreatorFactory;
    protected final Blockchain blockchain;
    private final AgentEventQueue eventQueue;
    private final AgentExecutors agentExecutors;

    private long blockAddedObserverId;
    private final AtomicReference<State> state = new AtomicReference<>(State.IDLE);

    public AgentMiningCoordinator(
            final AgentExecutors agentExecutors,
            final AgentController controller,
            final AgentProcessor agentProcessor,
            final AgentBlockCreatorFactory blockCreatorFactory,
            final Blockchain blockchain,
            final AgentEventQueue eventQueue) {
        this.agentExecutors = agentExecutors;
        this.controller = controller;
        this.agentProcessor = agentProcessor;
        this.blockCreatorFactory = blockCreatorFactory;
        this.eventQueue = eventQueue;
        this.blockchain = blockchain;
    }

    @Override
    public void start(){
        if (state.compareAndSet(State.IDLE, State.RUNNING)){
            agentExecutors.start();
            blockAddedObserverId = blockchain.observeBlockAdded(this);
            controller.start();
            agentExecutors.executeAgentProcessor(agentProcessor);
        }
    }

    @Override
    public void stop(){
        if (state.compareAndSet(State.RUNNING, State.STOPPED)){
            blockchain.removeObserver(blockAddedObserverId);
            agentProcessor.stop();;

            try{
                agentProcessor.awaitStop();
            } catch (final InterruptedException e){
                LOG.debug("Interrupted while waitting for AgentProcessor to stop");
                Thread.currentThread().interrupt();
            }
            agentExecutors.stop();
        }
    }

    @Override
    public void awaitStop() throws InterruptedException {
        agentExecutors.awaitStop();
    }

    @Override
    public boolean enable() {return true;}
    @Override
    public boolean disable() {
        return false;
    }

    @Override
    public boolean isMining() {
        return true;
    }

    @Override
    public Wei getMinTransactionGasPrice() {
        return blockCreatorFactory.getMinTransactionGasPrice();
    }

    @Override
    public void setExtraData(final Bytes extraData) {
        blockCreatorFactory.setExtraData(extraData);
    }

    @Override
    public Optional<Address> getCoinbase() {
        return Optional.of(blockCreatorFactory.getLocalAddress());
    }

    @Override
    public Optional<Block> createBlock(
            final BlockHeader parentHeader,
            final List<Transaction> transactions,
            final List<BlockHeader> ommers) {
        // One-off block creation has not been implemented
        return Optional.empty();
    }

    @Override
    public void onBlockAdded(final BlockAddedEvent event) {
        if (event.isNewCanonicalHead()) {
            LOG.trace("New canonical head detected");
            eventQueue.add(new AgentNewChainHead(event.getBlock().getHeader()));
        }
    }
}
