package com.adham.taskmanagement.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Complete task representation")
public record TaskResponse(
        @Schema(
                description = "Task identifier",
                example = "42",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String id,

        @Schema(
                description = "Task title",
                example = "Implement authentication",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(
                description = "Task description",
                example = "Verify JWT authentication for the API",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String description,

        @Schema(
                description = "Current workflow status",
                example = "IN_PROGRESS",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TaskStatus status,

        @Schema(
                description = "Email of the account that created the task",
                example = "owner@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String author,

        @Schema(
                description = "Assigned account email, or 'none' when unassigned",
                example = "assignee@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String assignee,

        @Schema(
                description = "Optimistic-lock version represented by the ETag header",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        long version,

        @JsonProperty("created_at")
        @Schema(
                description = "UTC creation timestamp",
                example = "2026-09-22T18:00:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instant createdAt,

        @JsonProperty("updated_at")
        @Schema(
                description = "UTC timestamp of the latest change",
                example = "2026-09-22T18:15:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instant updatedAt
) {
}
