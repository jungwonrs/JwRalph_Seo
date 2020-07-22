package org.hyperledger.besu.consensus.ibft;

import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.BlockHeaderBuilder;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.core.Util;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.tuweni.bytes.Bytes;

public class AgentBlockHashing {

    public static Hash calculateDataHashForCommittedSeal(
            final BlockHeader header, final AgentExtraData agentExtraData) {
        return Hash.hash(serializeHeader(header, agentExtraData::encodeWithoutCommitSeals));
    }

    public static Hash calculateDataHashForCommittedSeal(final BlockHeader header) {
        final AgentExtraData agentExtraData = AgentExtraData.decode(header);
        return Hash.hash(serializeHeader(header, agentExtraData::encodeWithoutCommitSeals));
    }

    public static Hash calculateHashOfAgentBlockOnChain(final BlockHeader header) {
        final AgentExtraData agentExtraData = AgentExtraData.decode(header);
        return Hash.hash(serializeHeader(header, agentExtraData::encodeWithoutCommitSealsAndRoundNumber));
    }

    public static List<Address> recoverCommitterAddresses(
            final BlockHeader header, final AgentExtraData agentExtraData) {
        final Hash committerHash =
                AgentBlockHashing.calculateDataHashForCommittedSeal(header, agentExtraData);

        return agentExtraData.getSeals().stream()
                .map(p -> Util.signatureToAddress(p, committerHash))
                .collect(Collectors.toList());
    }

    private static Bytes serializeHeader(
            final BlockHeader header, final Supplier<Bytes> extraDataSerializer) {

        // create a block header which is a copy of the header supplied as parameter except of the
        // extraData field
        final BlockHeaderBuilder builder = BlockHeaderBuilder.fromHeader(header);
        builder.blockHeaderFunctions(AgentBlockHeaderFunctions.forOnChainBlock());

        // set the extraData field using the supplied extraDataSerializer if the block height is not 0
        if (header.getNumber() == BlockHeader.GENESIS_BLOCK_NUMBER) {
            builder.extraData(header.getExtraData());
        } else {
            builder.extraData(extraDataSerializer.get());
        }

        final BytesValueRLPOutput out = new BytesValueRLPOutput();
        builder.buildBlockHeader().writeTo(out);
        return out.encoded();
    }



}
