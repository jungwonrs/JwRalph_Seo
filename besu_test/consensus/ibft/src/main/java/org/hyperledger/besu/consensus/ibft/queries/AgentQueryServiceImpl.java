package org.hyperledger.besu.consensus.ibft.queries;

import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.consensus.common.PoaQueryServiceImpl;
import org.hyperledger.besu.consensus.ibft.AgentBlockInterface;
import org.hyperledger.besu.consensus.ibft.AgentBlockHashing;
import org.hyperledger.besu.consensus.ibft.AgentExtraData;
import org.hyperledger.besu.crypto.NodeKey;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.plugin.data.Address;
import org.hyperledger.besu.plugin.services.query.AgentQueryService;

import java.util.Collection;
import java.util.Collections;

public class AgentQueryServiceImpl extends PoaQueryServiceImpl implements AgentQueryService {

    public AgentQueryServiceImpl(
            final AgentBlockInterface blockInterface, final Blockchain blockchain, final NodeKey nodeKey) {
        super(blockInterface, blockchain, nodeKey);
    }

    @Override
    public int getRoundNumberFrom(final org.hyperledger.besu.plugin.data.BlockHeader header) {
        final BlockHeader headerFromChain = getHeaderFromChain(header);
        final AgentExtraData extraData = AgentExtraData.decode(headerFromChain);
        return extraData.getRound();
    }

    @Override
    public Collection<Address> getSignersFrom(
            final org.hyperledger.besu.plugin.data.BlockHeader header) {
        final BlockHeader headerFromChain = getHeaderFromChain(header);
        final AgentExtraData extraData = AgentExtraData.decode(headerFromChain);

        return Collections.unmodifiableList(
                AgentBlockHashing.recoverCommitterAddresses(headerFromChain, extraData));
    }

    private BlockHeader getHeaderFromChain(
            final org.hyperledger.besu.plugin.data.BlockHeader header) {
        if (header instanceof BlockHeader) {
            return (BlockHeader) header;
        }

        final Hash blockHash = Hash.wrap(Bytes32.wrap(header.getBlockHash().toArray()));
        return getBlockchain().getBlockHeader(blockHash).orElseThrow();
    }
}