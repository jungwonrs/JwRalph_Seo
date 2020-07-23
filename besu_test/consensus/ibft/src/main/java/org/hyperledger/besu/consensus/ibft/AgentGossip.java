package org.hyperledger.besu.consensus.ibft;

import com.google.common.collect.Lists;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentOnMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentProposalMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentPrepareMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentCommitMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentRoundChangeMessageData;
import org.hyperledger.besu.consensus.ibft.network.ValidatorMulticaster;
import org.hyperledger.besu.consensus.ibft.payload.Authored;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.Message;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;

import java.util.List;

public class AgentGossip implements Gossiper {

    private final ValidatorMulticaster multicaster;

    public AgentGossip(final ValidatorMulticaster multicaster){
        this.multicaster = multicaster;
    }

    @Override
    public void send(final Message message){
        final MessageData messageData = message.getData();
        final Authored decodedMessage;
        switch (messageData.getCode()){
            case AgentMessageData.AGENT_ON:
                decodedMessage = AgentOnMessageData.fromMessageData(messageData).decode();
                break;
            case AgentMessageData.PROPOSAL:
                decodedMessage = AgentProposalMessageData.fromMessageData(messageData).decode();
                break;

            case AgentMessageData.PREPARE:
                decodedMessage = AgentPrepareMessageData.fromMessageData(messageData).decode();
                break;

            case AgentMessageData.COMMIT:
                decodedMessage = AgentCommitMessageData.fromMessageData(messageData).decode();
                break;

            case AgentMessageData.ROUND_CHANGE:
                decodedMessage = AgentRoundChangeMessageData.fromMessageData(messageData).decode();
                break;
            default:
                throw new IllegalArgumentException(
                        "Received message does not conform to any recognised Agent message structure");
        }
        final List<Address> excludeAddressesList =
                Lists.newArrayList(
                        message.getConnection().getPeerInfo().getAddress(), decodedMessage.getAuthor());

        multicaster.send(messageData, excludeAddressesList);
    }
}
