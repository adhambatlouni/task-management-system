package com.adham.taskmanagement.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "JWT access-token creation")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    @Operation(
            summary = "Create a JWT access token",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "JWT access token created"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Email or password is incorrect",
                    content = @Content
            )
    })
    public ResponseEntity<TokenResponse> token(
            @Parameter(hidden = true) Authentication authentication
    ) {

        String token = tokenService.generateToken(authentication);

        return ResponseEntity.ok(
                new TokenResponse(token)
        );
    }
}
