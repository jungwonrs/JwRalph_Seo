package org.hyperledger.besu.consensus.ibft.messagewrappers;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.payload.AgentPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.consensus.ibft.payload.Authored;
import org.hyperledger.besu.consensus.ibft.payload.RoundSpecific;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;

import java.util.StringJoiner;

import org.apache.tuweni.bytes.Bytes;

public class AgentMessage<P extends AgentPayload> implements Authored, RoundSpecific {

    private final AgentSignedData<P> payload;

    public AgentMessage(final AgentSignedData<P> payload){
        this.payload = payload;
    }

    @Override
    public Address getAuthor(){
        return payload.getAuthor();
    }

    @Override
    public ConsensusRoundIdentifier getRoundIdentifier() {return payload.getPayload().getRoundIdentifier();}

    public Bytes encode() {
        final BytesValueRLPOutput rlpOut = new BytesValueRLPOutput();
        payload.writeTo(rlpOut);
        return rlpOut.encoded();
    }

    public AgentSignedData<P> getSignedPayload() {
        return payload;
    }

    public int getMessageType() {
        return payload.getPayload().getMessageType();
    }

    protected P getPayload() {
        return payload.getPayload();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AgentMessage.class.getSimpleName() + "[", "]")
                .add("payload=" + payload)
                .toString();
    }
}