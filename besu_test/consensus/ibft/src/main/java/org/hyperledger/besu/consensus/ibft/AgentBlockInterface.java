package org.hyperledger.besu.consensus.ibft;

import org.hyperledger.besu.consensus.common.BlockInterface;
import org.hyperledger.besu.consensus.common.ValidatorVote;
import org.hyperledger.besu.consensus.common.VoteType;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.BlockHeaderBuilder;
import org.hyperledger.besu.ethereum.core.BlockHeaderFunctions;

import java.util.Collection;
import java.util.Optional;

public class AgentBlockInterface implements BlockInterface {

    @Override
    public Address getProposerOfBlock(final BlockHeader header) {
        return header.getCoinbase();
    }

    @Override
    public Address getProposerOfBlock(final org.hyperledger.besu.plugin.data.BlockHeader header) {
        return Address.fromHexString(header.getCoinbase().toHexString());
    }

    @Override
    public Optional<ValidatorVote> extractVoteFromHeader(final BlockHeader header) {
        final AgentExtraData agentExtraData = AgentExtraData.decode(header);

        if (agentExtraData.getVote().isPresent()) {
            final Vote headerVote = agentExtraData.getVote().get();
            final ValidatorVote vote =
                    new ValidatorVote(
                            headerVote.isAuth() ? VoteType.ADD : VoteType.DROP,
                            getProposerOfBlock(header),
                            headerVote.getRecipient());
            return Optional.of(vote);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Address> validatorsInBlock(final BlockHeader header) {
        final AgentExtraData agentExtraData = AgentExtraData.decode(header);
        return agentExtraData.getValidators();
    }

    public static Block replaceRoundInBlock(
            final Block block, final int round, final BlockHeaderFunctions blockHeaderFunctions) {
        final AgentExtraData prevExtraData = AgentExtraData.decode(block.getHeader());
        final AgentExtraData substituteExtraData =
                new AgentExtraData(
                        prevExtraData.getVanityData(),
                        prevExtraData.getSeals(),
                        prevExtraData.getVote(),
                        round,
                        prevExtraData.getValidators());

        final BlockHeaderBuilder headerBuilder = BlockHeaderBuilder.fromHeader(block.getHeader());
        headerBuilder
                .extraData(substituteExtraData.encode())
                .blockHeaderFunctions(blockHeaderFunctions);

        final BlockHeader newHeader = headerBuilder.buildBlockHeader();

        return new Block(newHeader, block.getBody());
    }


}
