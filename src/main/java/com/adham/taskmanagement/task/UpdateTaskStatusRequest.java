package com.adham.taskmanagement.task;

import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(

        @NotNull
        TaskStatus status

) {
}
