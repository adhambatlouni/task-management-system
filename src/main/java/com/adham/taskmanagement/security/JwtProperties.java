package com.adham.taskmanagement.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties("app.security.jwt")
public record JwtProperties(
        @NotBlank String secretBase64,
        @NotNull Duration accessTokenTtl
) {

    public JwtProperties {
        if (accessTokenTtl != null && !accessTokenTtl.isPositive()) {
            throw new IllegalArgumentException(
                    "JWT access-token TTL must be positive"
            );
        }
    }
}
