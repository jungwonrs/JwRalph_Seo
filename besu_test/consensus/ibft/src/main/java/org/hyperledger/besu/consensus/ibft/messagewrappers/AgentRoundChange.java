package org.hyperledger.besu.consensus.ibft.messagewrappers;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.AgentBlockHeaderFunctions;
import org.hyperledger.besu.consensus.ibft.payload.AgentPreparedCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangePayload;
import org.hyperledger.besu.consensus.ibft.payload.PreparedCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;
import org.hyperledger.besu.ethereum.rlp.RLP;
import org.hyperledger.besu.ethereum.rlp.RLPInput;

import java.util.Optional;

public class AgentRoundChange extends AgentMessage<AgentRoundChangePayload> {
private final Optional<Block> proposedBlock;

    public AgentRoundChange(
            final AgentSignedData<AgentRoundChangePayload> payload, final Optional<Block> proposedBlock) {
        super(payload);
        this.proposedBlock = proposedBlock;
    }

    public Optional<Block> getProposedBlock() {
        return proposedBlock;
    }

    public Optional<AgentPreparedCertificate> getPreparedCertificate() {
        return getPayload().getPreparedCertificate();
    }

    public Optional<ConsensusRoundIdentifier> getPreparedCertificateRound() {
        return getPreparedCertificate()
                .map(prepCert -> prepCert.getProposalPayload().getPayload().getRoundIdentifier());
    }

    @Override
    public Bytes encode() {
        final BytesValueRLPOutput rlpOut = new BytesValueRLPOutput();
        rlpOut.startList();
        getSignedPayload().writeTo(rlpOut);
        if (proposedBlock.isPresent()) {
            proposedBlock.get().writeTo(rlpOut);
        } else {
            rlpOut.writeNull();
        }
        rlpOut.endList();
        return rlpOut.encoded();
    }

    public static AgentRoundChange decode(final Bytes data) {

        final RLPInput rlpIn = RLP.input(data);
        rlpIn.enterList();
        final AgentSignedData<AgentRoundChangePayload> payload =
                AgentSignedData.readSignedRoundChangePayloadFrom(rlpIn);
        Optional<Block> block = Optional.empty();
        if (!rlpIn.nextIsNull()) {
            block = Optional.of(Block.readFrom(rlpIn, AgentBlockHeaderFunctions.forCommittedSeal()));
        } else {
            rlpIn.skipNext();
        }
        rlpIn.leaveList();

        return new AgentRoundChange(payload, block);
    }
}
