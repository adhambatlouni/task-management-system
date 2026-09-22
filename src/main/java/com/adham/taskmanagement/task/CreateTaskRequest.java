package com.adham.taskmanagement.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Information needed to create a task")
public record CreateTaskRequest(

        @Schema(
                description = "Short task summary",
                example = "Implement authentication",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        String title,

        @Schema(
                description = "Detailed description of the work",
                example = "Verify JWT authentication for the API",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        String description

) {
}
