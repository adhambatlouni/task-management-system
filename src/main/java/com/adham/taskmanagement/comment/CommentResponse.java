package com.adham.taskmanagement.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "A comment attached to a task")
public record CommentResponse(

        @Schema(
                description = "Comment identifier",
                example = "7",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String id,

        @JsonProperty("task_id")
        @Schema(
                description = "Identifier of the commented task",
                example = "42",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String taskId,

        @Schema(
                description = "Comment text",
                example = "Authentication has been verified",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String text,

        @Schema(
                description = "Email of the account that wrote the comment",
                example = "assignee@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String author,

        @JsonProperty("created_at")
        @Schema(
                description = "UTC creation timestamp",
                example = "2026-09-22T18:20:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instant createdAt,

        @JsonProperty("updated_at")
        @Schema(
                description = "UTC timestamp of the latest change",
                example = "2026-09-22T18:20:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instant updatedAt

) {
}
