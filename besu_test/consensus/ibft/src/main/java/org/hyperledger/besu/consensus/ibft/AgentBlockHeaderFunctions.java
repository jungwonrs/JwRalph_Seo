package org.hyperledger.besu.consensus.ibft;

import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.BlockHeaderFunctions;
import org.hyperledger.besu.ethereum.core.Hash;

import java.util.function.Function;

public class AgentBlockHeaderFunctions implements BlockHeaderFunctions {

    private static final AgentBlockHeaderFunctions COMMITTED_SEAL =
            new AgentBlockHeaderFunctions(AgentBlockHashing::calculateDataHashForCommittedSeal);
    private static final AgentBlockHeaderFunctions ON_CHAIN =
            new AgentBlockHeaderFunctions(AgentBlockHashing::calculateHashOfIbftBlockOnChain);

    private final Function<BlockHeader, Hash> hashFunction;

    private AgentBlockHeaderFunctions(final Function<BlockHeader, Hash> hashFunction) {
        this.hashFunction = hashFunction;
    }

    public static BlockHeaderFunctions forOnChainBlock() {
        return ON_CHAIN;
    }

    public static BlockHeaderFunctions forCommittedSeal() {
        return COMMITTED_SEAL;
    }

    @Override
    public Hash hash(final BlockHeader header) {
        return hashFunction.apply(header);
    }

    @Override
    public AgentExtraData parseExtraData(final BlockHeader header) {
        return AgentExtraData.decodeRaw(header.getExtraData());
    }
}
