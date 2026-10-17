package com.brainics.backend.auth.controller;

import com.brainics.backend.auth.dto.CreateUserRequest;
import com.brainics.backend.auth.dto.UserResponse;
import com.brainics.backend.auth.service.UserManagementService;
import com.brainics.backend.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Validated
public class AdminUserController {
    private final UserManagementService userManagementService;

    public AdminUserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(userManagementService.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully", userManagementService.create(request)));
    }

    @PatchMapping("/{id}/enabled")
    public ResponseEntity<ApiResponse<UserResponse>> setEnabled(@PathVariable long id,
                                                                 @Valid @RequestBody EnabledRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("User status updated", userManagementService.setEnabled(id, request.enabled())));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<UserResponse>> resetPassword(@PathVariable long id,
                                                                    @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully",
                userManagementService.resetPassword(id, request.newPassword())));
    }

    public record EnabledRequest(@NotNull Boolean enabled) { }
    public record ResetPasswordRequest(@NotBlank @Size(min = 12, max = 72) String newPassword) { }
}
