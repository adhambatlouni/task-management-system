package com.adham.taskmanagement.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "New task assignment")
public record AssignTaskRequest(

        @Schema(
                description = "Registered assignee email, or 'none' to unassign the task",
                example = "assignee@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Pattern(
                regexp = "(?i)(none|[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,})"
        )
        String assignee

) {
}
