package com.adham.taskmanagement.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TaskListResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        String author,
        String assignee,

        @JsonProperty("total_comments")
        long totalComments,

        @JsonProperty("created_at")
        Instant createdAt,

        @JsonProperty("updated_at")
        Instant updatedAt
) {
}
