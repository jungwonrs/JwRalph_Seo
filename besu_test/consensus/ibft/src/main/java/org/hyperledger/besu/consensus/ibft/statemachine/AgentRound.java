package org.hyperledger.besu.consensus.ibft.statemachine;

import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.AgentBlockHashing;
import org.hyperledger.besu.consensus.ibft.AgentBlockHeaderFunctions;
import org.hyperledger.besu.consensus.ibft.AgentBlockInterface;
import org.hyperledger.besu.consensus.ibft.AgentExtraData;
import org.hyperledger.besu.consensus.ibft.AgentHelper;
import org.hyperledger.besu.consensus.ibft.AgentRoundTimer;
import org.hyperledger.besu.consensus.ibft.blockcreation.AgentBlockCreator;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentCommit;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentPrepare;
import org.hyperledger.besu.consensus.ibft.messagewrappers.AgentProposal;
import org.hyperledger.besu.consensus.ibft.network.AgentMessageTransmitter;
import org.hyperledger.besu.consensus.ibft.payload.AgentMessageFactory;
import org.hyperledger.besu.consensus.ibft.payload.AgentRoundChangeCertificate;
import org.hyperledger.besu.crypto.NodeKey;
import org.hyperledger.besu.crypto.SECP256K1.Signature;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.chain.MinedBlockObserver;
import org.hyperledger.besu.ethereum.core.Block;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.BlockImporter;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.mainnet.HeaderValidationMode;
import org.hyperledger.besu.plugin.services.securitymodule.SecurityModuleException;
import org.hyperledger.besu.util.Subscribers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class AgentRound {

    private static final Logger LOG = LogManager.getLogger();

    private final Subscribers<MinedBlockObserver> observers;
    private final AgentRoundState roundState;
    private final AgentBlockCreator blockCreator;
    private final ProtocolContext protocolContext;
    private final BlockImporter blockImporter;
    private final NodeKey nodeKey;
    private final AgentMessageFactory messageFactory; // used only to create stored local msgs
    private final AgentMessageTransmitter transmitter;

    public AgentRound(
            final AgentRoundState roundState,
            final AgentBlockCreator blockCreator,
            final ProtocolContext protocolContext,
            final BlockImporter blockImporter,
            final Subscribers<MinedBlockObserver> observers,
            final NodeKey nodeKey,
            final AgentMessageFactory messageFactory,
            final AgentMessageTransmitter transmitter,
            final AgentRoundTimer roundTimer) {
        this.roundState = roundState;
        this.blockCreator = blockCreator;
        this.protocolContext = protocolContext;
        this.blockImporter = blockImporter;
        this.observers = observers;
        this.nodeKey = nodeKey;
        this.messageFactory = messageFactory;
        this.transmitter = transmitter;

        roundTimer.startTimer(getRoundIdentifier());
    }

    public ConsensusRoundIdentifier getRoundIdentifier() {
        return roundState.getRoundIdentifier();
    }

    public void createAndSendProposalMessage(final long headerTimeStampSeconds) {
        final Block block = blockCreator.createBlock(headerTimeStampSeconds);
        final AgentExtraData extraData = AgentExtraData.decode(block.getHeader());
        LOG.debug("Creating proposed block. round={}", roundState.getRoundIdentifier());
        LOG.trace(
                "Creating proposed block with extraData={} blockHeader={}", extraData, block.getHeader());
        updateStateWithProposalAndTransmit(block, Optional.empty());
    }

    public void startRoundWith(
            final AgentRoundChangeArtifacts roundChangeArtifacts, final long headerTimestamp) {
        final Optional<Block> bestBlockFromRoundChange = roundChangeArtifacts.getBlock();

        final AgentRoundChangeCertificate roundChangeCertificate =
                roundChangeArtifacts.getRoundChangeCertificate();
        Block blockToPublish;
        if (!bestBlockFromRoundChange.isPresent()) {
            LOG.debug("Sending proposal with new block. round={}", roundState.getRoundIdentifier());
            blockToPublish = blockCreator.createBlock(headerTimestamp);
        } else {
            LOG.debug(
                    "Sending proposal from PreparedCertificate. round={}", roundState.getRoundIdentifier());
            blockToPublish =
                    AgentBlockInterface.replaceRoundInBlock(
                            bestBlockFromRoundChange.get(),
                            getRoundIdentifier().getRoundNumber(),
                            AgentBlockHeaderFunctions.forCommittedSeal());
        }

        updateStateWithProposalAndTransmit(blockToPublish, Optional.of(roundChangeCertificate));
    }

    private void updateStateWithProposalAndTransmit(
            final Block block, final Optional<AgentRoundChangeCertificate> roundChangeCertificate) {
        final AgentProposal proposal;
        try {
            proposal = messageFactory.createProposal(getRoundIdentifier(), block, roundChangeCertificate);
        } catch (final SecurityModuleException e) {
            LOG.warn("Failed to create a signed Proposal, waiting for next round.", e);
            return;
        }

        transmitter.multicastProposal(
                proposal.getRoundIdentifier(), proposal.getBlock(), proposal.getRoundChangeCertificate());
        updateStateWithProposedBlock(proposal);
    }

    public void handleProposalMessage(final AgentProposal msg) {
        LOG.debug("Received a proposal message. round={}", roundState.getRoundIdentifier());
        final Block block = msg.getBlock();

        if (updateStateWithProposedBlock(msg)) {
            LOG.debug("Sending prepare message. round={}", roundState.getRoundIdentifier());
            try {
                final AgentPrepare localPrepareMessage =
                        messageFactory.createPrepare(getRoundIdentifier(), block.getHash());
                peerIsPrepared(localPrepareMessage);
                transmitter.multicastPrepare(
                        localPrepareMessage.getRoundIdentifier(), localPrepareMessage.getDigest());
            } catch (final SecurityModuleException e) {
                LOG.warn("Failed to create a signed Prepare; {}", e.getMessage());
            }
        }
    }

    public void handlePrepareMessage(final AgentPrepare msg) {
        LOG.debug("Received a prepare message. round={}", roundState.getRoundIdentifier());
        peerIsPrepared(msg);
    }

    public void handleCommitMessage(final AgentCommit msg) {
        LOG.debug("Received a commit message. round={}", roundState.getRoundIdentifier());
        peerIsCommitted(msg);
    }

    public Optional<AgentPreparedRoundArtifacts> constructPreparedRoundArtifacts() {
        return roundState.constructPreparedRoundArtifacts();
    }

    private boolean updateStateWithProposedBlock(final AgentProposal msg) {
        final boolean wasPrepared = roundState.isPrepared();
        final boolean wasCommitted = roundState.isCommitted();
        final boolean blockAccepted = roundState.setProposedBlock(msg);

        if (blockAccepted) {
            final Block block = roundState.getProposedBlock().get();

            final Signature commitSeal;
            try {
                commitSeal = createCommitSeal(block);
            } catch (final SecurityModuleException e) {
                LOG.warn("Failed to construct commit seal; {}", e.getMessage());
                return blockAccepted;
            }

            // There are times handling a proposed block is enough to enter prepared.
            if (wasPrepared != roundState.isPrepared()) {
                LOG.debug("Sending commit message. round={}", roundState.getRoundIdentifier());
                transmitter.multicastCommit(getRoundIdentifier(), block.getHash(), commitSeal);
            }

            // can automatically add _our_ commit message to the roundState
            // cannot create a prepare message here, as it may be _our_ proposal, and thus we cannot also
            // prepare
            try {
                final AgentCommit localCommitMessage =
                        messageFactory.createCommit(
                                roundState.getRoundIdentifier(), msg.getBlock().getHash(), commitSeal);
                roundState.addCommitMessage(localCommitMessage);
            } catch (final SecurityModuleException e) {
                LOG.warn("Failed to create signed Commit message; {}", e.getMessage());
                return blockAccepted;
            }

            // It is possible sufficient commit seals are now available and the block should be imported
            if (wasCommitted != roundState.isCommitted()) {
                importBlockToChain();
            }
        }

        return blockAccepted;
    }

    private void peerIsPrepared(final AgentPrepare msg) {
        final boolean wasPrepared = roundState.isPrepared();
        roundState.addPrepareMessage(msg);
        if (wasPrepared != roundState.isPrepared()) {
            LOG.debug("Sending commit message. round={}", roundState.getRoundIdentifier());
            final Block block = roundState.getProposedBlock().get();
            try {
                transmitter.multicastCommit(getRoundIdentifier(), block.getHash(), createCommitSeal(block));
                // Note: the local-node's commit message was added to RoundState on block acceptance
                // and thus does not need to be done again here.
            } catch (final SecurityModuleException e) {
                LOG.warn("Failed to construct a commit seal: {}", e.getMessage());
            }
        }
    }

    private void peerIsCommitted(final AgentCommit msg) {
        final boolean wasCommitted = roundState.isCommitted();
        roundState.addCommitMessage(msg);
        if (wasCommitted != roundState.isCommitted()) {
            importBlockToChain();
        }
    }

    private void importBlockToChain() {
        final Block blockToImport =
                AgentHelper.createSealedBlock(
                        roundState.getProposedBlock().get(), roundState.getCommitSeals());

        final long blockNumber = blockToImport.getHeader().getNumber();
        final AgentExtraData extraData = AgentExtraData.decode(blockToImport.getHeader());
        LOG.info(
                "Importing block to chain. round={}, hash={}",
                getRoundIdentifier(),
                blockToImport.getHash());
        LOG.trace("Importing block with extraData={}", extraData);
        final boolean result =
                blockImporter.importBlock(protocolContext, blockToImport, HeaderValidationMode.FULL);
        if (!result) {
            LOG.error(
                    "Failed to import block to chain. block={} extraData={} blockHeader={}",
                    blockNumber,
                    extraData,
                    blockToImport.getHeader());
        } else {
            notifyNewBlockListeners(blockToImport);
        }
    }

    private Signature createCommitSeal(final Block block) {
        final BlockHeader proposedHeader = block.getHeader();
        final AgentExtraData extraData = AgentExtraData.decode(proposedHeader);
        final Hash commitHash =
                AgentBlockHashing.calculateDataHashForCommittedSeal(proposedHeader, extraData);
        return nodeKey.sign(commitHash);
    }

    private void notifyNewBlockListeners(final Block block) {
        observers.forEach(obs -> obs.blockMined(block));
    }
}

