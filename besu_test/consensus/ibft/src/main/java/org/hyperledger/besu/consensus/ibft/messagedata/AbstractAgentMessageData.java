package org.hyperledger.besu.consensus.ibft.messagedata;

import org.apache.tuweni.bytes.Bytes;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.AbstractMessageData;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

import java.util.function.Function;

public abstract class AbstractAgentMessageData extends AbstractMessageData {

    protected AbstractAgentMessageData(final Bytes data) {
        super(data);
    }

    protected static <T extends AbstractAgentMessageData> T fromMessageData(
            final MessageData messageData,
            final int messageCode,
            final Class<T> clazz,
            final Function<Bytes, T> constructor) {
        if (clazz.isInstance(messageData)) {
            @SuppressWarnings("unchecked")
            T castMessage = (T) messageData;
            return castMessage;
        }
        final int code = messageData.getCode();
        if (code != messageCode) {
            throw new IllegalArgumentException(
                    String.format(
                            "MessageData has code %d and thus is not a %s", code, clazz.getSimpleName()));
        }

        return constructor.apply(messageData.getData());
    }
}

