package org.hyperledger.besu.consensus.ibft.payload;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.rlp.BytesValueRLPOutput;
import org.hyperledger.besu.ethereum.rlp.RLPInput;
import org.hyperledger.besu.ethereum.rlp.RLPOutput;

public interface AgentPayload extends RoundSpecific {

    void writeTo(final RLPOutput rlpOutput);

    default Bytes encoded() {
        BytesValueRLPOutput rlpOutput = new BytesValueRLPOutput();
        writeTo(rlpOutput);

        return rlpOutput.encoded();
    }

    int getMessageType();

    static Hash readDigest(final RLPInput agentMessageData) {
        return Hash.wrap(agentMessageData.readBytes32());
    }
}
