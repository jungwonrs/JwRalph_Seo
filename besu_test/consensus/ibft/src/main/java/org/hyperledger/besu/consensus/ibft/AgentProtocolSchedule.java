package org.hyperledger.besu.consensus.ibft;

import static org.hyperledger.besu.consensus.ibft.AgentBlockHeaderValidationRulesetFactory.agentBlockHeaderValidator;

import org.hyperledger.besu.config.AgentConfigOptions;
import org.hyperledger.besu.config.GenesisConfigOptions;
import org.hyperledger.besu.ethereum.MainnetBlockValidator;
import org.hyperledger.besu.ethereum.core.PrivacyParameters;
import org.hyperledger.besu.ethereum.core.Wei;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;
import org.hyperledger.besu.ethereum.mainnet.ProtocolScheduleBuilder;
import org.hyperledger.besu.ethereum.mainnet.MainnetBlockImporter;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSpecBuilder;
import org.hyperledger.besu.ethereum.mainnet.MainnetBlockBodyValidator;


import java.math.BigInteger;

public class AgentProtocolSchedule {

    private static final BigInteger DEFAULT_CHAIN_ID = BigInteger.ONE;

    public static ProtocolSchedule create(
            final GenesisConfigOptions config,
            final PrivacyParameters privacyParameters,
            final boolean isRevertReasonEnabled) {
        final AgentConfigOptions agentConfig = config.getAgentConfigOptions();
        final long blockPeriod = agentConfig.getBlockPeriodSeconds();

        return new ProtocolScheduleBuilder(
                config,
                DEFAULT_CHAIN_ID,
                builder -> applyAgentChanges(blockPeriod, builder),
                privacyParameters,
                isRevertReasonEnabled)
                .createProtocolSchedule();
    }

    public static ProtocolSchedule create(
            final GenesisConfigOptions config, final boolean isRevertReasonEnabled){
        return create(config, PrivacyParameters.DEFAULT, isRevertReasonEnabled);
    }

    public static ProtocolSchedule create(final GenesisConfigOptions config){
        return create(config, PrivacyParameters.DEFAULT, false);
    }

    private static ProtocolSpecBuilder applyAgentChanges(
            final long secondsBetweenBlocks, final ProtocolSpecBuilder builder
    ){
        return builder
                .blockHeaderValidatorBuilder(agentBlockHeaderValidator(secondsBetweenBlocks))
                .ommerHeaderValidatorBuilder(agentBlockHeaderValidator(secondsBetweenBlocks))
                .blockBodyValidatorBuilder(MainnetBlockBodyValidator::new)
                .blockValidatorBuilder(MainnetBlockValidator::new)
                .blockImporterBuilder(MainnetBlockImporter::new)
                .difficultyCalculator((time, parent, protocolContext) -> BigInteger.ONE)
                .blockReward(Wei.ZERO)
                .skipZeroBlockRewards(true)
                .blockHeaderFunctions(IbftBlockHeaderFunctions.forOnChainBlock());
    }


}
