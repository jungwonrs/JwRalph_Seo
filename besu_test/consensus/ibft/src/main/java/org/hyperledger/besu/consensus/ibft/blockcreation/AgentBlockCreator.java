package org.hyperledger.besu.consensus.ibft.blockcreation;


import org.hyperledger.besu.consensus.ibft.AgentBlockHeaderFunctions;
import org.hyperledger.besu.consensus.ibft.AgentHelper;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.blockcreation.AbstractBlockCreator;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.BlockHeaderBuilder;
import org.hyperledger.besu.ethereum.core.SealableBlockHeader;
import org.hyperledger.besu.ethereum.core.Wei;
import org.hyperledger.besu.ethereum.eth.transactions.PendingTransactions;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;

import java.util.function.Function;


public class AgentBlockCreator extends AbstractBlockCreator {
    public AgentBlockCreator(
            final Address localAddress,
            final ExtraDataCalculator extraDataCalculator,
            final PendingTransactions pendingTransactions,
            final ProtocolContext protocolContext,
            final ProtocolSchedule protocolSchedule,
            final Function<Long, Long> gasLimitCalculator,
            final Wei minTransactionGasPrice,
            final Double minBlockOccupancyRatio,
            final BlockHeader parentHeader){
        super(
                localAddress,
                extraDataCalculator,
                pendingTransactions,
                protocolContext,
                protocolSchedule,
                gasLimitCalculator,
                minTransactionGasPrice,
                localAddress,
                minBlockOccupancyRatio,
                parentHeader
        );
    }

    @Override
    protected BlockHeader createFinalBlockHeader(final SealableBlockHeader sealableBlockHeader){
        final BlockHeaderBuilder builder =
                BlockHeaderBuilder.create()
                .populateFrom(sealableBlockHeader)
                .mixHash(AgentHelper.EXPECTED_MIX_HASH)
                .nonce(0L)
                .blockHeaderFunctions(AgentBlockHeaderFunctions.forCommittedSeal());
        return builder.buildBlockHeader();
    }





}
