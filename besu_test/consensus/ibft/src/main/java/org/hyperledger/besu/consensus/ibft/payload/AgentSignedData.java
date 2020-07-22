package org.hyperledger.besu.consensus.ibft.payload;

import org.hyperledger.besu.crypto.SECP256K1.Signature;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.Util;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;
import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

import java.util.Objects;
import java.util.StringJoiner;

import org.apache.tuweni.bytes.Bytes;


public class AgentSignedData<M extends AgentPayload> implements Authored {

    private final Address sender;
    private final Signature signature;
    private final M unsignedPayload;

    public AgentSignedData(final M unsignedPayload, final Address sender, final Signature signature) {
        this.unsignedPayload = unsignedPayload;
        this.sender = sender;
        this.signature = signature;
    }

    @Override
    public Address getAuthor() {
        return sender;
    }

    public M getPayload() {
        return unsignedPayload;
    }

    public void writeTo(final RLPOutput output) {

        output.startList();
        unsignedPayload.writeTo(output);
        output.writeBytes(signature.encodedBytes());
        output.endList();
    }

    public Bytes encode() {
        final BytesValueRLPOutput rlpEncode = new BytesValueRLPOutput();
        writeTo(rlpEncode);
        return rlpEncode.encoded();
    }

    public static AgentSignedData<AgentOnPayload> readSignedAgentOnPayloadFrom(final RLPInput rlpInput) {

        rlpInput.enterList();
        final AgentOnPayload unsignedMessageData = AgentOnPayload.readFrom(rlpInput);
        final Signature signature = readSignature(rlpInput);
        rlpInput.leaveList();

        return from(unsignedMessageData, signature);
    }



    public static AgentSignedData<AgentProposalPayload> readSignedProposalPayloadFrom(final RLPInput rlpInput) {

        rlpInput.enterList();
        final AgentProposalPayload unsignedMessageData = AgentProposalPayload.readFrom(rlpInput);
        final Signature signature = readSignature(rlpInput);
        rlpInput.leaveList();

        return from(unsignedMessageData, signature);
    }

    public static AgentSignedData<AgentPreparePayload> readSignedPreparePayloadFrom(final RLPInput rlpInput) {

        rlpInput.enterList();
        final AgentPreparePayload unsignedMessageData = AgentPreparePayload.readFrom(rlpInput);
        final Signature signature = readSignature(rlpInput);
        rlpInput.leaveList();

        return from(unsignedMessageData, signature);
    }


    public static AgentSignedData<AgentCommitPayload> readSignedCommitPayloadFrom(final RLPInput rlpInput) {

        rlpInput.enterList();
        final AgentCommitPayload unsignedMessageData = AgentCommitPayload.readFrom(rlpInput);
        final Signature signature = readSignature(rlpInput);
        rlpInput.leaveList();

        return from(unsignedMessageData, signature);
    }

    public static AgentSignedData<AgentRoundChangePayload> readSignedRoundChangePayloadFrom(
            final RLPInput rlpInput) {

        rlpInput.enterList();
        final AgentRoundChangePayload unsignedMessageData = AgentRoundChangePayload.readFrom(rlpInput);
        final Signature signature = readSignature(rlpInput);
        rlpInput.leaveList();

        return from(unsignedMessageData, signature);
    }


    protected static <M extends AgentPayload> AgentSignedData<M> from(
            final M unsignedMessageData, final Signature signature) {

        final Address sender = recoverSender(unsignedMessageData, signature);

        return new AgentSignedData<>(unsignedMessageData, sender, signature);
    }

    protected static Signature readSignature(final RLPInput signedMessage) {
        return signedMessage.readBytes(Signature::decode);
    }

    protected static Address recoverSender(
            final AgentPayload unsignedMessageData, final Signature signature) {

        return Util.signatureToAddress(signature, AgentMessageFactory.hashForSignature(unsignedMessageData));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgentSignedData<?> that = (AgentSignedData<?>) o;
        return Objects.equals(sender, that.sender)
                && Objects.equals(signature, that.signature)
                && Objects.equals(unsignedPayload, that.unsignedPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, signature, unsignedPayload);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgentSignedData.class.getSimpleName() + "[", "]")
                .add("sender=" + sender)
                .add("signature=" + signature)
                .add("unsignedPayload=" + unsignedPayload)
                .toString();
    }
}

