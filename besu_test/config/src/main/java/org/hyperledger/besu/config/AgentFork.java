package org.hyperledger.besu.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class AgentFork {
    private static final String FORK_BLOCK_KEY = "block";
    private static final String VALIDATORS_KEY = "validators";
    private static final String BLOCK_PERIOD_SECONDS_KEY = "blockperiodseconds";

    private final ObjectNode forkConfigRoot;

    @JsonCreator
    public AgentFork(final ObjectNode forkConfigRoot) {
        this.forkConfigRoot = forkConfigRoot;
    }

    public long getForkBlock() {
        return JsonUtil.getLong(forkConfigRoot, FORK_BLOCK_KEY)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Fork block not specified for IBFT2 fork in custom forks"));
    }

    public OptionalInt getBlockPeriodSeconds() {
        return JsonUtil.getInt(forkConfigRoot, BLOCK_PERIOD_SECONDS_KEY);
    }

    public Optional<List<String>> getValidators() throws IllegalArgumentException {
        final Optional<ArrayNode> validatorNode = JsonUtil.getArrayNode(forkConfigRoot, VALIDATORS_KEY);

        if (validatorNode.isEmpty()) {
            return Optional.empty();
        }

        List<String> validators = Lists.newArrayList();
        validatorNode
                .get()
                .elements()
                .forEachRemaining(
                        value -> {
                            if (!value.isTextual()) {
                                throw new IllegalArgumentException(
                                        "Ibft Validator fork does not contain a string " + value.toString());
                            }

                            validators.add(value.asText());
                        });
        return Optional.of(validators);
    }
}

