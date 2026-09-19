package com.adham.taskmanagement.task;

public record TaskResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        String author,
        String assignee
) {
}
