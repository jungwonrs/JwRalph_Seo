package org.hyperledger.besu.consensus.ibft.messagewrappers;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.AgentBlockHeaderFunctions;
import org.hyperledger.besu.consensus.ibft.payload.AgentOnPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentProposalPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangeCertificate;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;
import org.hyperledger.besu.ethereum.rlp.RLP;
import org.hyperledger.besu.ethereum.rlp.RLPInput;

import java.util.Optional;

public class AgentOn extends AgentMessage<AgentOnPayload> {

        private final Block proposedBlock;

        private final Optional<AgentRoundChangeCertificate> roundChangeCertificate;

        public AgentOn(
                final AgentSignedData<AgentOnPayload> payload,
                final Block proposedBlock,
                final Optional<AgentRoundChangeCertificate> certificate) {
            super(payload);
            this.proposedBlock = proposedBlock;
            this.roundChangeCertificate = certificate;
        }

        public Block getBlock() {
            return proposedBlock;
        }

        public Hash getDigest() {
            return getPayload().getDigest();
        }

        public Optional<AgentRoundChangeCertificate> getRoundChangeCertificate() {
            return roundChangeCertificate;
        }

        @Override
        public Bytes encode() {
            final BytesValueRLPOutput rlpOut = new BytesValueRLPOutput();
            rlpOut.startList();
            getSignedPayload().writeTo(rlpOut);
            proposedBlock.writeTo(rlpOut);
            if (roundChangeCertificate.isPresent()) {
                roundChangeCertificate.get().writeTo(rlpOut);
            } else {
                rlpOut.writeNull();
            }
            rlpOut.endList();
            return rlpOut.encoded();
        }

        public static AgentOn decode(final Bytes data) {
            final RLPInput rlpIn = RLP.input(data);
            rlpIn.enterList();
            final AgentSignedData<AgentOnPayload> payload = AgentSignedData.readSignedAgentOnPayloadFrom(rlpIn);
            final Block proposedBlock = Block.readFrom(rlpIn, AgentBlockHeaderFunctions.forCommittedSeal());

            final Optional<AgentRoundChangeCertificate> roundChangeCertificate =
                    readRoundChangeCertificate(rlpIn);

            rlpIn.leaveList();
            return new AgentOn(payload, proposedBlock, roundChangeCertificate);
        }

        private static Optional<AgentRoundChangeCertificate> readRoundChangeCertificate(final RLPInput rlpIn) {
            AgentRoundChangeCertificate roundChangeCertificate = null;
            if (!rlpIn.nextIsNull()) {
                roundChangeCertificate = AgentRoundChangeCertificate.readFrom(rlpIn);
            } else {
                rlpIn.skipNext();
            }

            return Optional.ofNullable(roundChangeCertificate);
        }
}
