package org.hyperledger.besu.consensus.ibft.blockcreation;

import org.hyperledger.besu.consensus.common.ConsensusHelpers;
import org.hyperledger.besu.consensus.common.ValidatorVote;
import org.hyperledger.besu.consensus.common.VoteTally;
import org.hyperledger.besu.consensus.ibft.AgentExtraData;
import org.hyperledger.besu.consensus.ibft.Vote;
import org.hyperledger.besu.consensus.ibft.AgentContext;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.MiningParameters;
import org.hyperledger.besu.ethereum.core.Wei;
import org.hyperledger.besu.ethereum.eth.transactions.PendingTransactions;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.tuweni.bytes.Bytes;


public class AgentBlockCreatorFactory {
    private final Function<Long, Long> gasLimitCalculator;
    private final PendingTransactions pendingTransactions;
    private final ProtocolContext protocolContext;
    private final ProtocolSchedule protocolSchedule;
    private final Address localAddress;

    private volatile Bytes vanityData;
    private volatile Wei minTransactionGasPrice;
    private volatile Double minBlockOccupancyRatio;


    public AgentBlockCreatorFactory(
            final Function<Long, Long> gasLimitCalculator,
            final PendingTransactions pendingTransactions,
            final ProtocolContext protocolContext,
            final ProtocolSchedule protocolSchedule,
            final MiningParameters miningParams,
            final Address localAddress) {
        this.gasLimitCalculator = gasLimitCalculator;
        this.pendingTransactions = pendingTransactions;
        this.protocolContext = protocolContext;
        this.protocolSchedule = protocolSchedule;
        this.localAddress = localAddress;
        this.minTransactionGasPrice = miningParams.getMinTransactionGasPrice();
        this.minBlockOccupancyRatio = miningParams.getMinBlockOccupancyRatio();
        this.vanityData = miningParams.getExtraData();
    }


    public AgentBlockCreator create(final BlockHeader parentHeader, final int round){
        return new AgentBlockCreator(
                localAddress,
                ph -> createExtraData(round, ph),
                pendingTransactions,
                protocolContext,
                protocolSchedule,
                gasLimitCalculator,
                minTransactionGasPrice,
                minBlockOccupancyRatio,
                parentHeader);
    }

    public void setExtraData(final Bytes extraData){this.vanityData = extraData.copy();}

    public void setMinTransactionGasPrice(final Wei minTransactionGasPrice){
        this.minTransactionGasPrice = minTransactionGasPrice;
    }

    public Wei getMinTransactionGasPrice() {return minTransactionGasPrice;}

    public Bytes createExtraData(final int round, final BlockHeader parentHeader){
        final VoteTally voteTally =
                protocolContext
                .getConsensusState(AgentContext.class)
                .getVoteTallyCache()
                .getVoteTallyAfterBlock(parentHeader);

        final Optional<ValidatorVote> proposal =
                protocolContext
                .getConsensusState(AgentContext.class)
                .getVoteProposer()
                .getVote(localAddress, voteTally);

        final List<Address> validators = new ArrayList<>(voteTally.getValidators());

        final AgentExtraData extraData =
                new AgentExtraData(
                        ConsensusHelpers.zeroLeftPad(vanityData, AgentExtraData.EXTRA_VANITY_LENGTH),
                        Collections.emptyList(),
                        toVote(proposal),
                        round,
                        validators);

        return extraData.encode();

    }


    public Address getLocalAddress() {return localAddress;}

    private static Optional<Vote> toVote(final Optional<ValidatorVote> input){
        return input
                .map(v -> Optional.of(new Vote(v.getRecipient(), v.getVotePolarity())))
                .orElse(Optional.empty());
    }

}
