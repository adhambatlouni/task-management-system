package com.adham.taskmanagement.common.openapi;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(
        name = "ApiProblem",
        description = "Standard API error response"
)
public record ApiProblemResponse(

        @Schema(
                description = "Stable, human-readable error category",
                example = "Validation failed",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,

        @Schema(
                description = "HTTP status code",
                example = "400",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        int status,

        @Schema(
                description = "Explanation of this specific failure",
                example = "One or more request fields are invalid",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String detail,

        @Schema(
                description = "Request path that produced the error",
                example = "/api/accounts",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String instance,

        @Schema(
                description = "Field errors, present only when request validation fails",
                nullable = true,
                example = "{\"email\": \"must be a well-formed email address\"}"
        )
        Map<String, String> errors
) {
}
