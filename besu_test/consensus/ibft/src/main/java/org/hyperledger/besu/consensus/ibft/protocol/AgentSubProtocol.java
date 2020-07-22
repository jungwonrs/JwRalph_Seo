package org.hyperledger.besu.consensus.ibft.protocol;

import org.hyperledger.besu.consensus.ibft.messagedata.AgentMessageData;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.Capability;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.SubProtocol;

public class AgentSubProtocol implements SubProtocol {
    public static String NAME = "AGENT";
    public static final Capability AGENT = Capability.create(NAME, 1);

    private static final AgentSubProtocol INSTANCE = new AgentSubProtocol();

    public static AgentSubProtocol get() {return INSTANCE;}

    @Override
    public String getName() {return  NAME;}

    @Override
    public int messageSpace(final int protocolVersion) {return AgentMessageData.MESSAGE_SPACE;}

    @Override
    public boolean isValidMessageCode(final int protocolVersion, final int code){
        switch (code){
            case AgentMessageData.AGENT_ON:
            case AgentMessageData.PROPOSAL:
            case AgentMessageData.COMMIT:
            case AgentMessageData.ROUND_CHANGE:
                return true;

            default:
                return false;
        }
    }

    @Override
    public String messageName(final int protocolVersion, final int code){
        switch (code){
            case AgentMessageData.AGENT_ON:
                return "AgentON";
            case AgentMessageData.PROPOSAL:
                return "Proposal";
            case AgentMessageData.PREPARE:
                return "Prepare";
            case AgentMessageData.COMMIT:
                return "Commit";
            case AgentMessageData.ROUND_CHANGE:
                return "RoundChange";
            default:
                return INVALID_MESSAGE_NAME;
        }
    }
}
