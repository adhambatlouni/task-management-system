package com.adham.taskmanagement.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credentials for a new account")
public record RegisterAccountRequest(

        @Schema(
                description = "Unique email used for authentication",
                example = "owner@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                description = "Account password with at least 6 characters",
                format = "password",
                example = "DemoPassword123!",
                accessMode = Schema.AccessMode.WRITE_ONLY,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(min = 6)
        String password

) {
}
