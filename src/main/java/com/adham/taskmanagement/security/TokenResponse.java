package com.adham.taskmanagement.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT credentials for protected API operations")
public record TokenResponse(
        @Schema(
                description = "Signed bearer token; submit it through the bearerAuth scheme",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvd25lckBleGFtcGxlLmNvbSJ9.signature",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String token
) {
}
