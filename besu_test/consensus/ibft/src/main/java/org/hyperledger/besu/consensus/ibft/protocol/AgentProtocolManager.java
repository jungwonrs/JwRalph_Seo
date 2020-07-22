package org.hyperledger.besu.consensus.ibft.protocol;

import org.hyperledger.besu.consensus.ibft.AgentEventQueue;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentEvent;
import org.hyperledger.besu.consensus.ibft.ibftevent.AgentEvents;
import org.hyperledger.besu.consensus.ibft.network.PeerConnectionTracker;
import org.hyperledger.besu.ethereum.p2p.network.ProtocolManager;
import org.hyperledger.besu.ethereum.p2p.rlpx.connections.PeerConnection;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.Capability;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.Message;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.messages.DisconnectMessage.DisconnectReason;

import java.util.Arrays;
import java.util.List;

public class AgentProtocolManager implements ProtocolManager {
    private final AgentEventQueue agentEventQueue;

    private final PeerConnectionTracker peers;

    public AgentProtocolManager(
            final AgentEventQueue agentEventQueue, final PeerConnectionTracker peers) {
     this.agentEventQueue = agentEventQueue;
     this.peers = peers;
    }

    @Override
    public String getSupportedProtocol(){ return AgentSubProtocol.get().getName();}

    @Override
    public List<Capability> getSupportedCapabilities() { return Arrays.asList(AgentSubProtocol.AGENT);}

    @Override
    public void stop(){}

    @Override
    public void awaitStop() throws InterruptedException {}

    @Override
    public void processMessage(final Capability cap, final Message message){
        final AgentEvent messageEvent = AgentEvents.fromMessage(message);
        agentEventQueue.add(messageEvent);
    }

    @Override
    public void handleNewConnection(final PeerConnection peerConnection){
        peers.add(peerConnection);
    }

    @Override
    public void handleDisconnect(

            final PeerConnection peerConnection,
            final DisconnectReason disconnectReason,
            final boolean initiatedByPeer){

        peers.remove(peerConnection);}
}
