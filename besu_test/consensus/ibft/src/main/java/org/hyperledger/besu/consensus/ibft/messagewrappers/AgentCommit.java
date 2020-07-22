package org.hyperledger.besu.consensus.ibft.messagewrappers;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.consensus.ibft.payload.AgentCommitPayload;
import org.hyperledger.besu.consensus.ibft.payload.AgentSignedData;
import org.hyperledger.besu.crypto.SECP256K1;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.rlp.RLP;

public class AgentCommit extends AgentMessage<AgentCommitPayload>{
    public AgentCommit(final AgentSignedData<AgentCommitPayload> payload) {
        super(payload);
    }

    public SECP256K1.Signature getCommitSeal() {
        return getPayload().getCommitSeal();
    }

    public Hash getDigest() {
        return getPayload().getDigest();
    }

    public static AgentCommit decode(final Bytes data) {
        return new AgentCommit(AgentSignedData.readSignedCommitPayloadFrom(RLP.input(data)));
    }
}