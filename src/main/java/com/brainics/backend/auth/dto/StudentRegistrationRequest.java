package com.brainics.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentRegistrationRequest(
        @NotBlank
        @Size(min = 3, max = 80)
        @Pattern(
                regexp = "^[A-Za-z0-9._-]+$",
                message = "Username may contain letters, numbers, dots, underscores and hyphens only"
        )
        String username,

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Size(min = 12, max = 72)
        String password
) {
}