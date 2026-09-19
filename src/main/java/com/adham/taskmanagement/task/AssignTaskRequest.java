package com.adham.taskmanagement.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AssignTaskRequest(

        @NotBlank
        @Pattern(
                regexp = "(?i)(none|[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,})"
        )
        String assignee

) {
}
