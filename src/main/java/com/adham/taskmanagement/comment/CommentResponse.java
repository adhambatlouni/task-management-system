package com.adham.taskmanagement.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CommentResponse(

        String id,

        @JsonProperty("task_id")
        String taskId,

        String text,

        String author,

        @JsonProperty("created_at")
        Instant createdAt,

        @JsonProperty("updated_at")
        Instant updatedAt

) {
}
