package com.adham.taskmanagement.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TaskResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        String author,
        String assignee,
        long version,

        @JsonProperty("created_at")
        Instant createdAt,

        @JsonProperty("updated_at")
        Instant updatedAt
) {
}
