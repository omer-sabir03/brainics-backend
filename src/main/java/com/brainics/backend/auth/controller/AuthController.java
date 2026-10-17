package com.brainics.backend.auth.controller;

import com.brainics.backend.auth.dto.AuthResponse;
import com.brainics.backend.auth.dto.LoginRequest;
import com.brainics.backend.auth.dto.UserResponse;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.auth.service.AuthService;
import com.brainics.backend.common.ApiResponse;
import com.brainics.backend.common.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(request.username(), request.password())));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        AppUser user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(user)));
    }
}
