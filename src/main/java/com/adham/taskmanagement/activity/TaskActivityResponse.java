package com.adham.taskmanagement.activity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TaskActivityResponse(
        String id,

        @JsonProperty("task_id")
        String taskId,

        TaskActivityType type,
        String actor,

        @JsonProperty("previous_value")
        String previousValue,

        @JsonProperty("new_value")
        String newValue,

        @JsonProperty("task_version")
        long taskVersion,

        @JsonProperty("created_at")
        Instant createdAt
) {
}
