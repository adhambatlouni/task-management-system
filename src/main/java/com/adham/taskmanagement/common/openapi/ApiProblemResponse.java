package com.adham.taskmanagement.common.openapi;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(
        name = "ApiProblem",
        description = "Standard API error response"
)
public record ApiProblemResponse(

        @Schema(description = "Short error category")
        String title,

        @Schema(description = "HTTP status code")
        int status,

        @Schema(description = "Explanation of this specific failure")
        String detail,

        @Schema(description = "Request path that produced the error")
        String instance,

        @Schema(
                description = "Field errors, present only when request validation fails",
                nullable = true
        )
        Map<String, String> errors
) {
}
