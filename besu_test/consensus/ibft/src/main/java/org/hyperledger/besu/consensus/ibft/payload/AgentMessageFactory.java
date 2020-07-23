package org.hyperledger.besu.consensus.ibft.payload;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentOn;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentPreparedRoundArtifacts;
import org.hyperledger.besu.crypto.NodeKey;
import org.hyperledger.besu.crypto.SECP256K1.Signature;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.core.Util;

import java.util.Optional;

import org.apache.tuweni.bytes.Bytes;
public class AgentMessageFactory {

    private final NodeKey nodeKey;

    public AgentMessageFactory(final NodeKey nodeKey){
        this.nodeKey = nodeKey;
    }

    public AgentOn createAgentOn(
            final ConsensusRoundIdentifier roundIdentifier,
            final Block block,
            final Optional<AgentRoundChangeCertificate> roundChangeCertificate){
        final AgentOnPayload payload = new AgentOnPayload(roundIdentifier, block.getHash());
        return new AgentOn(createSignedMessage(payload), block, roundChangeCertificate);
    }


    public AgentProposal createProposal(
            final ConsensusRoundIdentifier roundIdentifier,
            final Block block,
            final Optional<AgentRoundChangeCertificate> roundChangeCertificate) {

        final AgentProposalPayload payload = new AgentProposalPayload(roundIdentifier, block.getHash());

        return new AgentProposal(createSignedMessage(payload), block, roundChangeCertificate);
    }


    public AgentPrepare createPrepare(final ConsensusRoundIdentifier roundIdentifier, final Hash digest){
        final AgentPreparePayload payload = new AgentPreparePayload(roundIdentifier, digest);

        return new AgentPrepare(createSignedMessage(payload));
    }

    public AgentCommit createCommit(
            final ConsensusRoundIdentifier roundIdentifier,
            final Hash digest,
            final Signature commitSeal) {

        final AgentCommitPayload payload = new AgentCommitPayload(roundIdentifier, digest, commitSeal);

        return new AgentCommit(createSignedMessage(payload));
    }

    public AgentRoundChange createRoundChange(
            final ConsensusRoundIdentifier roundIdentifier,
            final Optional<AgentPreparedRoundArtifacts> preparedRoundArtifacts) {

        final AgentRoundChangePayload payload =
                new AgentRoundChangePayload(
                        roundIdentifier,
                        preparedRoundArtifacts.map(AgentPreparedRoundArtifacts::getPreparedCertificate));
        return new AgentRoundChange(
                createSignedMessage(payload), preparedRoundArtifacts.map(AgentPreparedRoundArtifacts::getBlock));
    }

    private <M extends AgentPayload> AgentSignedData<M> createSignedMessage(final M payload) {
        final Signature signature = nodeKey.sign(hashForSignature(payload));
        return new AgentSignedData<>(payload, Util.publicKeyToAddress(nodeKey.getPublicKey()), signature);
    }

    public static Hash hashForSignature(final AgentPayload unsignedMessageData) {
        return Hash.hash(
                Bytes.concatenate(
                        Bytes.of(unsignedMessageData.getMessageType()), unsignedMessageData.encoded()));
    }



}
