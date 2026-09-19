package com.adham.taskmanagement.task;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(

        @NotBlank
        String title,

        @NotBlank
        String description

) {
}
