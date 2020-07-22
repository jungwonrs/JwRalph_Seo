package org.hyperledger.besu.consensus.ibft.validation;

import org.hyperledger.besu.consensus.ibft.AgentHelper;
import org.hyperledger.besu.consensus.ibft.ConsensusRoundIdentifier;
import org.hyperledger.besu.consensus.ibft.AgentContext;
import org.hyperledger.besu.consensus.ibft.blockcreation.ProposerSelector;
import org.hyperledger.besu.ethereum.BlockValidator;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;

import java.util.Collection;

public class AgentMessageValidatorFactory {
    private final ProposerSelector proposerSelector;
    private final ProtocolContext protocolContext;
    private final ProtocolSchedule protocolSchedule;

    public AgentMessageValidatorFactory(
            final ProposerSelector proposerSelector,
            final ProtocolSchedule protocolSchedule,
            final ProtocolContext protocolContext) {
        this.proposerSelector = proposerSelector;
        this.protocolSchedule = protocolSchedule;
        this.protocolContext = protocolContext;
    }

    private Collection<Address> getValidatorsAfterBlock(final BlockHeader parentHeader) {
        return protocolContext
                .getConsensusState(AgentContext.class)
                .getVoteTallyCache()
                .getVoteTallyAfterBlock(parentHeader)
                .getValidators();
    }

    private AgentSignedDataValidator createSignedDataValidator(
            final ConsensusRoundIdentifier roundIdentifier, final BlockHeader parentHeader) {

        return new AgentSignedDataValidator(
                getValidatorsAfterBlock(parentHeader),
                proposerSelector.selectProposerForRound(roundIdentifier),
                roundIdentifier);
    }

    public AgentMessageValidator createMessageValidator(
            final ConsensusRoundIdentifier roundIdentifier, final BlockHeader parentHeader) {
        final BlockValidator blockValidator =
                protocolSchedule.getByBlockNumber(roundIdentifier.getSequenceNumber()).getBlockValidator();
        final Collection<Address> validators = getValidatorsAfterBlock(parentHeader);

        return new AgentMessageValidator(
                createSignedDataValidator(roundIdentifier, parentHeader),
                new AgentProposalBlockConsistencyValidator(),
                blockValidator,
                protocolContext,
                new AgentRoundChangeCertificateValidator(
                        validators,
                        (ri) -> createSignedDataValidator(ri, parentHeader),
                        roundIdentifier.getSequenceNumber()));
    }

    public AgentRoundChangeMessageValidator createRoundChangeMessageValidator(
            final long chainHeight, final BlockHeader parentHeader) {
        final Collection<Address> validators = getValidatorsAfterBlock(parentHeader);

        return new AgentRoundChangeMessageValidator(
                new AgentRoundChangePayloadValidator(
                        (roundIdentifier) -> createSignedDataValidator(roundIdentifier, parentHeader),
                        validators,
                        AgentHelper.prepareMessageCountForQuorum(
                                AgentHelper.calculateRequiredValidatorQuorum(validators.size())),
                        chainHeight),
                new AgentProposalBlockConsistencyValidator());
    }

    public AgentFutureRoundProposalMessageValidator createFutureRoundProposalMessageValidator(
            final long chainHeight, final BlockHeader parentHeader) {

        return new AgentFutureRoundProposalMessageValidator(this, chainHeight, parentHeader);
    }
}
