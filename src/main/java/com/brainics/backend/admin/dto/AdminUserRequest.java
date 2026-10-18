package com.brainics.backend.admin.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 80, message = "Username must be between 3 and 80 characters")
    private String username;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;  // ADMIN, TEACHER, STUDENT

    private String firstName;
    private String lastName;

    @Pattern(regexp = "^(\\+92|0)[0-9]{10}$", message = "Invalid phone format")
    private String phone;

    @Builder.Default
    private Boolean enabled = true;
}
