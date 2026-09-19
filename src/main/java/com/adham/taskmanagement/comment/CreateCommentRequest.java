package com.adham.taskmanagement.comment;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(

        @NotBlank
        String text

) {
}
