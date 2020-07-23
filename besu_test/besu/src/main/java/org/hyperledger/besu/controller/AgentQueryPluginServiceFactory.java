package org.hyperledger.besu.controller;

import org.hyperledger.besu.consensus.ibft.AgentBlockInterface;
import org.hyperledger.besu.consensus.ibft.queries.AgentQueryServiceImpl;
import org.hyperledger.besu.crypto.NodeKey;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.plugin.services.metrics.PoAMetricsService;
import org.hyperledger.besu.plugin.services.query.AgentQueryService;
import org.hyperledger.besu.plugin.services.query.PoaQueryService;
import org.hyperledger.besu.services.BesuPluginContextImpl;

public class AgentQueryPluginServiceFactory implements PluginServiceFactory {

    private final Blockchain blockchain;
    private final NodeKey nodeKey;

    public AgentQueryPluginServiceFactory(final Blockchain blockchain, final NodeKey nodeKey){
        this.blockchain = blockchain;
        this.nodeKey = nodeKey;
    }

    @Override
    public void appendPluginServices(final BesuPluginContextImpl besuContext) {
        final AgentBlockInterface blockInterface = new AgentBlockInterface();

        final AgentQueryServiceImpl service =
                new AgentQueryServiceImpl(blockInterface, blockchain, nodeKey);
        besuContext.addService(AgentQueryService.class, service);
        besuContext.addService(PoaQueryService.class, service);
        besuContext.addService(PoAMetricsService.class, service);



    }
}
