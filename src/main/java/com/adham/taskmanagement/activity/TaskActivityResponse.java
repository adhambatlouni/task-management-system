package com.adham.taskmanagement.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "One append-only task history entry")
public record TaskActivityResponse(
        @Schema(
                description = "Activity identifier",
                example = "9",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String id,

        @JsonProperty("task_id")
        @Schema(
                description = "Identifier of the affected task",
                example = "42",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String taskId,

        @Schema(
                description = "Recorded change type",
                example = "STATUS_CHANGED",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TaskActivityType type,

        @Schema(
                description = "Email of the account that performed the change",
                example = "assignee@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String actor,

        @JsonProperty("previous_value")
        @Schema(
                description = "Value before the change; null for task creation",
                example = "CREATED",
                nullable = true
        )
        String previousValue,

        @JsonProperty("new_value")
        @Schema(
                description = "Value after the change",
                example = "IN_PROGRESS",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String newValue,

        @JsonProperty("task_version")
        @Schema(
                description = "Task version produced by the change",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        long taskVersion,

        @JsonProperty("created_at")
        @Schema(
                description = "UTC timestamp when the change was recorded",
                example = "2026-09-22T18:15:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instant createdAt
) {
}
