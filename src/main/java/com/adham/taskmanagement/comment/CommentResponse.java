package com.adham.taskmanagement.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentResponse(

        String id,

        @JsonProperty("task_id")
        String taskId,

        String text,

        String author

) {
}
