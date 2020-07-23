package org.hyperledger.besu.consensus.ibft;

import org.apache.tuweni.units.bigints.UInt256;
import org.hyperledger.besu.consensus.ibft.headervalidationrules.AgentCoinbaseValidationRule;
import org.hyperledger.besu.consensus.ibft.headervalidationrules.AgentCommitSealsValidationRule;
import org.hyperledger.besu.consensus.ibft.headervalidationrules.AgentValidatorsValidationRule;
import org.hyperledger.besu.ethereum.core.BlockHeader;
import org.hyperledger.besu.ethereum.core.Hash;
import org.hyperledger.besu.ethereum.mainnet.BlockHeaderValidator;
import org.hyperledger.besu.ethereum.mainnet.headervalidationrules.AncestryValidationRule;
import org.hyperledger.besu.ethereum.mainnet.headervalidationrules.GasLimitRangeAndDeltaValidationRule;
import org.hyperledger.besu.ethereum.mainnet.headervalidationrules.GasUsageValidationRule;
import org.hyperledger.besu.ethereum.mainnet.headervalidationrules.TimestampBoundedByFutureParameter;
import org.hyperledger.besu.ethereum.mainnet.headervalidationrules.TimestampMoreRecentThanParent;
import org.hyperledger.besu.ethereum.mainnet.headervalidationrules.ConstantFieldValidationRule;
public class AgentBlockHeaderValidationRulesetFactory {

    public static BlockHeaderValidator.Builder agentBlockHeaderValidator(
            final long secondsBetweenBlocks
    ){
        return new BlockHeaderValidator.Builder()
                .addRule(new AncestryValidationRule())
                .addRule(new GasUsageValidationRule())
                .addRule(new GasLimitRangeAndDeltaValidationRule(5000, 0x7fffffffffffffffL))
                .addRule(new TimestampBoundedByFutureParameter(1))
                .addRule(new TimestampMoreRecentThanParent(secondsBetweenBlocks))
                .addRule(
                        new ConstantFieldValidationRule<>(
                                "MixHash", BlockHeader::getMixHash, IbftHelpers.EXPECTED_MIX_HASH))
                .addRule(
                        new ConstantFieldValidationRule<>(
                                "OmmersHash", BlockHeader::getOmmersHash, Hash.EMPTY_LIST_HASH))
                .addRule(
                        new ConstantFieldValidationRule<>(
                                "Difficulty", BlockHeader::getDifficulty, UInt256.ONE))
                .addRule(new ConstantFieldValidationRule<>("Nonce", BlockHeader::getNonce, 0L))
                .addRule(new AgentValidatorsValidationRule())
                .addRule(new AgentCoinbaseValidationRule())
                .addRule(new AgentCommitSealsValidationRule());
    }
}
