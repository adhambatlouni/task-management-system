package com.adham.taskmanagement.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterAccountRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6)
        String password

) {
}
