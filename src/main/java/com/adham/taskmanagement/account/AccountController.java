package com.adham.taskmanagement.account;

import com.adham.taskmanagement.common.openapi.ApiProblemResponse;
import com.adham.taskmanagement.common.openapi.OpenApiExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@Tag(
        name = "Accounts",
        description = "Create an account with a unique email address"
)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Register an account",
            description = "Creates an account with a normalized unique email and a BCrypt-hashed password."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account registered",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Registration details are invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.REGISTRATION_VALIDATION
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email is already registered",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(
                                    value = OpenApiExamples.CONFLICT
                            )
                    )
            )
    })
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterAccountRequest request
    ) {
        accountService.register(request);

        return ResponseEntity.ok().build();
    }
}
