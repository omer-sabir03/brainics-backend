package com.brainics.backend.admin.controller;

import com.brainics.backend.admin.dto.AdminUserRequest;
import com.brainics.backend.admin.dto.AdminUserResponse;
import com.brainics.backend.admin.service.AdminUserService;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserResponse> createUser(
            @Valid @RequestBody AdminUserRequest request,
            Authentication authentication) {
        log.info("Creating new admin user: {}", request.getUsername());
            Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();

        Optional<AppUser> byUsername = userRepository.findByUsername(username);
        Long actorId = byUsername.isPresent()?byUsername.get().getId(): -1;
        AdminUserResponse response = adminUserService.createUser(request, actorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRequest request,
            Authentication authentication) {
        log.info("Updating admin user: {}", userId);
            Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();

        Optional<AppUser> byUsername = userRepository.findByUsername(username);
        Long actorId = byUsername.isPresent()?byUsername.get().getId(): -1;
        AdminUserResponse response = adminUserService.updateUser(userId, request, actorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("Deleting admin user: {}", userId);
     //       Long actorId = (Long) authentication.getPrincipal();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();

        Optional<AppUser> byUsername = userRepository.findByUsername(username);
        Long actorId = byUsername.isPresent()?byUsername.get().getId(): -1;
        adminUserService.deleteUser(userId, actorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long userId) {
        log.info("Fetching user: {}", userId);
        AdminUserResponse response = adminUserService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserResponse> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user by username: {}", username);
        AdminUserResponse response = adminUserService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AdminUserResponse> response = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminUserResponse>> getUsersByRole(
            @PathVariable String role,
            Pageable pageable) {
        log.info("Fetching users by role: {}", role);
        Page<AdminUserResponse> response = adminUserService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableUser(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("Enabling user: {}", userId);
//        Long actorId = (Long) authentication.getPrincipal();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();

        Optional<AppUser> byUsername = userRepository.findByUsername(username);
        Long actorId = byUsername.isPresent()?byUsername.get().getId(): -1;
        adminUserService.enableUser(userId, actorId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableUser(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("Disabling user: {}", userId);
    //    Long actorId = (Long) authentication.getPrincipal();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();

        Optional<AppUser> byUsername = userRepository.findByUsername(username);
        Long actorId = byUsername.isPresent()?byUsername.get().getId(): -1;
        adminUserService.disableUser(userId, actorId);
        return ResponseEntity.ok().build();
    }
}
