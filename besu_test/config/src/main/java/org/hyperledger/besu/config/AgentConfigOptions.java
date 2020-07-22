package org.hyperledger.besu.config;

import java.util.Map;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;


public class AgentConfigOptions {

    public static final AgentConfigOptions DEFAULT =
            new AgentConfigOptions(JsonUtil.createEmptyObjectNode());

    private static final long DEFAULT_EPOCH_LENGTH = 30_000;
    private static final int DEFAULT_BLOCK_PERIOD_SECONDS = 1;
    private static final int DEFAULT_ROUND_EXPIRY_SECONDS = 1;
    private static final int DEFAULT_GOSSIPED_HISTORY_LIMIT = 1000;
    private static final int DEFAULT_MESSAGE_QUEUE_LIMIT = 1000;
    private static final int DEFAULT_DUPLICATE_MESSAGE_LIMIT = 100;
    private static final int DEFAULT_FUTURE_MESSAGES_LIMIT = 1000;
    private static final int DEFAULT_FUTURE_MESSAGES_MAX_DISTANCE = 10;

    private final ObjectNode agentConfigRoot;

    AgentConfigOptions(final ObjectNode agentConfigRoot){
        this.agentConfigRoot = agentConfigRoot;
    }

    public long getEpochLength() {
        return JsonUtil.getLong(agentConfigRoot, "epochlength", DEFAULT_EPOCH_LENGTH);
    }

    public int getBlockPeriodSeconds() {
        return JsonUtil.getInt(agentConfigRoot, "blockperiodseconds", DEFAULT_BLOCK_PERIOD_SECONDS);
    }

    public int getRequestTimeoutSeconds() {
        return JsonUtil.getInt(agentConfigRoot, "requesttimeoutseconds", DEFAULT_ROUND_EXPIRY_SECONDS);
    }

    public int getGossipedHistoryLimit() {
        return JsonUtil.getInt(agentConfigRoot, "gossipedhistorylimit", DEFAULT_GOSSIPED_HISTORY_LIMIT);
    }

    public int getMessageQueueLimit() {
        return JsonUtil.getInt(agentConfigRoot, "messagequeuelimit", DEFAULT_MESSAGE_QUEUE_LIMIT);
    }

    public int getDuplicateMessageLimit() {
        return JsonUtil.getInt(
                agentConfigRoot, "duplicatemessagelimit", DEFAULT_DUPLICATE_MESSAGE_LIMIT);
    }

    public int getFutureMessagesLimit() {
        return JsonUtil.getInt(agentConfigRoot, "futuremessageslimit", DEFAULT_FUTURE_MESSAGES_LIMIT);
    }

    public int getFutureMessagesMaxDistance() {
        return JsonUtil.getInt(
                agentConfigRoot, "futuremessagesmaxdistance", DEFAULT_FUTURE_MESSAGES_MAX_DISTANCE);
    }

    Map<String, Object> asMap() {
        final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        if (agentConfigRoot.has("epochlength")) {
            builder.put("epochLength", getEpochLength());
        }
        if (agentConfigRoot.has("blockperiodseconds")) {
            builder.put("blockPeriodSeconds", getBlockPeriodSeconds());
        }
        if (agentConfigRoot.has("requesttimeoutseconds")) {
            builder.put("requestTimeoutSeconds", getRequestTimeoutSeconds());
        }
        if (agentConfigRoot.has("gossipedhistorylimit")) {
            builder.put("gossipedHistoryLimit", getGossipedHistoryLimit());
        }
        if (agentConfigRoot.has("messagequeuelimit")) {
            builder.put("messageQueueLimit", getMessageQueueLimit());
        }
        if (agentConfigRoot.has("duplicatemessagelimit")) {
            builder.put("duplicateMessageLimit", getDuplicateMessageLimit());
        }
        if (agentConfigRoot.has("futuremessageslimit")) {
            builder.put("futureMessagesLimit", getFutureMessagesLimit());
        }
        if (agentConfigRoot.has("futuremessagesmaxdistance")) {
            builder.put("futureMessagesMaxDistance", getFutureMessagesMaxDistance());
        }
        return builder.build();
    }


}
