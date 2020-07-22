package org.hyperledger.besu.consensus.ibft.messagewrappers;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.payload.AgentPreparePayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.consensus.ibft.payload.PreparePayload;
import org.hyperledger.besu.consensus.ibft.payload.SignedData;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.rlp.RLP;

public class AgentPrepare extends AgentMessage<AgentPreparePayload> {
    public AgentPrepare(final AgentSignedData<AgentPreparePayload> payload) {
        super(payload);
    }

    public Hash getDigest() {
        return getPayload().getDigest();
    }

    public static AgentPrepare decode(final Bytes data) {
        return new AgentPrepare(AgentSignedData.readSignedPreparePayloadFrom(RLP.input(data)));
    }
}
