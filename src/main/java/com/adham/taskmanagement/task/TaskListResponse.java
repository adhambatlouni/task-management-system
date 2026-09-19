package com.adham.taskmanagement.task;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskListResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        String author,
        String assignee,

        @JsonProperty("total_comments")
        long totalComments
) {
}
