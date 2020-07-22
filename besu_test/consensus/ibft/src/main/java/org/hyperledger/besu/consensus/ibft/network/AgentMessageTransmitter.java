package org.hyperledger.besu.consensus.ibft.network;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;

import org.hyperledger.besu.consensus.ibft.messagedata.AgentOnMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentProposalMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentPrepareMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentCommitMessageData;
import org.hyperledger.besu.consensus.ibft.messagedata.AgentRoundChangeMessageData;

import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentOn;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentRoundChange;

import org.hyperledger.besu.consensus.ibft.payload.AgentMessageFactory;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangeCertificate;
import org.hyperledger.besu.consensus.ibft.statemachine.AgentPreparedRoundArtifacts;
import org.hyperledger.besu.crypto.SECP256K1.Signature;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.plugin.services.securitymodule.SecurityModuleException;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AgentMessageTransmitter {
    private static final Logger LOG =  LogManager.getLogger();

    private final AgentMessageFactory messageFactory;
    private final ValidatorMulticaster multicaster;

    public AgentMessageTransmitter(
            final AgentMessageFactory messageFactory, final ValidatorMulticaster multicaster)
    {
        this.messageFactory = messageFactory;
        this.multicaster = multicaster;
    }

    public void multicastAgentOn(
            final ConsensusRoundIdentifier roundIdentifier,
            final Block block,
            final Optional<AgentRoundChangeCertificate> roundChangeCertificate){
        try{
            final AgentOn data =
                    messageFactory.createAgentOn(roundIdentifier, block, roundChangeCertificate);
            final AgentOnMessageData message = AgentOnMessageData.create(data);

            multicaster.send(message);
        } catch (final SecurityModuleException e){
            LOG.warn("Failed to generate signature for AgentOn (not sent): {}", e.getMessage());
        }
    }

    public void multicastProposal(
            final ConsensusRoundIdentifier roundIdentifier,
            final Block block,
            final Optional<AgentRoundChangeCertificate> roundChangeCertificate) {
        try {
            final AgentProposal data =
                    messageFactory.createProposal(roundIdentifier, block, roundChangeCertificate);

            final AgentProposalMessageData message = AgentProposalMessageData.create(data);

            multicaster.send(message);
        } catch (final SecurityModuleException e) {
            LOG.warn("Failed to generate signature for Proposal (not sent): {} ", e.getMessage());
        }
    }

    public void multicastPrepare(final ConsensusRoundIdentifier roundIdentifier, final Hash digest) {
        try {
            final AgentPrepare data = messageFactory.createPrepare(roundIdentifier, digest);

            final AgentPrepareMessageData message = AgentPrepareMessageData.create(data);

            multicaster.send(message);
        } catch (final SecurityModuleException e) {
            LOG.warn("Failed to generate signature for Prepare (not sent): {} ", e.getMessage());
        }
    }

    public void multicastCommit(
            final ConsensusRoundIdentifier roundIdentifier,
            final Hash digest,
            final Signature commitSeal) {
        try {
            final AgentCommit data = messageFactory.createCommit(roundIdentifier, digest, commitSeal);

            final AgentCommitMessageData message = AgentCommitMessageData.create(data);

            multicaster.send(message);
        } catch (final SecurityModuleException e) {
            LOG.warn("Failed to generate signature for Commit (not sent): {} ", e.getMessage());
        }
    }

    public void multicastRoundChange(
            final ConsensusRoundIdentifier roundIdentifier,
            final Optional<AgentPreparedRoundArtifacts> preparedRoundArtifacts) {
        try {
            final AgentRoundChange data =
                    messageFactory.createRoundChange(roundIdentifier, preparedRoundArtifacts);

            final AgentRoundChangeMessageData message = AgentRoundChangeMessageData.create(data);

            multicaster.send(message);
        } catch (final SecurityModuleException e) {
            LOG.warn("Failed to generate signature for RoundChange (not sent): {} ", e.getMessage());
        }
    }




}
