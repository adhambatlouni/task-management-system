package com.adham.taskmanagement.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "New task workflow status")
public record UpdateTaskStatusRequest(

        @Schema(
                description = "Status to apply to the task",
                example = "IN_PROGRESS",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        TaskStatus status

) {
}
