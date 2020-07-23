package org.hyperledger.besu.consensus.ibft;

import org.hyperledger.besu.consensus.ibft.payload.AgentPreparedCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangePayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.crypto.SECP256K1;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.core.Util;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.BlockHeaderBuilder;

import java.util.Collection;
import java.util.Optional;

public class AgentHelper {
    public static final Hash EXPECTED_MIX_HASH =
            Hash.fromHexString("0x63746963616c2062797a616e74696e65206661756c7420746f6c6572616e6365");

    public static int calculateRequiredValidatorQuorum(final int validatorCount) {
        return Util.fastDivCeiling(2 * validatorCount, 3);
    }

    public static long prepareMessageCountForQuorum(final long quorum) {
        return quorum - 1;
    }

    public static Block createSealedBlock(
            final Block block, final Collection<SECP256K1.Signature> commitSeals) {
        final BlockHeader initialHeader = block.getHeader();
        final AgentExtraData initialExtraData = AgentExtraData.decode(initialHeader);

        final AgentExtraData sealedExtraData =
                new AgentExtraData(
                        initialExtraData.getVanityData(),
                        commitSeals,
                        initialExtraData.getVote(),
                        initialExtraData.getRound(),
                        initialExtraData.getValidators());

        final BlockHeader sealedHeader =
                BlockHeaderBuilder.fromHeader(initialHeader)
                        .extraData(sealedExtraData.encode())
                        .blockHeaderFunctions(IbftBlockHeaderFunctions.forOnChainBlock())
                        .buildBlockHeader();

        return new Block(sealedHeader, block.getBody());
    }

    public static Optional<AgentPreparedCertificate> findLatestPreparedCertificate(
            final Collection<AgentSignedData<AgentRoundChangePayload>> msgs) {

        Optional<AgentPreparedCertificate> result = Optional.empty();

        for (AgentSignedData<AgentRoundChangePayload> roundChangeMsg : msgs) {
            final AgentRoundChangePayload payload = roundChangeMsg.getPayload();
            if (payload.getPreparedCertificate().isPresent()) {
                if (!result.isPresent()) {
                    result = payload.getPreparedCertificate();
                } else {
                    final AgentPreparedCertificate currentLatest = result.get();
                    final AgentPreparedCertificate nextCert = payload.getPreparedCertificate().get();

                    if (currentLatest.getProposalPayload().getPayload().getRoundIdentifier().getRoundNumber()
                            < nextCert.getProposalPayload().getPayload().getRoundIdentifier().getRoundNumber()) {
                        result = Optional.of(nextCert);
                    }
                }
            }
        }
        return result;
    }
}


