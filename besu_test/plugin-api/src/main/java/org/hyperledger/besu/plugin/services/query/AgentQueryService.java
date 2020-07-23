package org.hyperledger.besu.plugin.services.query;

import org.hyperledger.besu.plugin.data.Address;
import org.hyperledger.besu.plugin.data.BlockHeader;

import java.util.Collection;

public interface AgentQueryService extends PoaQueryService {

    int getRoundNumberFrom(final BlockHeader header);

    Collection<Address> getSignersFrom(final BlockHeader header);

}
