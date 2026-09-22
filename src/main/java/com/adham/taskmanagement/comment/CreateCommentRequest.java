package com.adham.taskmanagement.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Comment to attach to a task")
public record CreateCommentRequest(

        @Schema(
                description = "Comment text",
                example = "Authentication has been verified",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        String text

) {
}
