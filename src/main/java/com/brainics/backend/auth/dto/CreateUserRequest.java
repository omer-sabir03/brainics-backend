package com.brainics.backend.auth.dto;

import com.brainics.backend.auth.model.RoleName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 80) String username,
        @NotBlank @Size(min = 12, max = 72) String password,
        @NotNull RoleName role) { }
