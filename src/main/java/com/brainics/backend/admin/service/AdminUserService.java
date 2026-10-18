package com.brainics.backend.admin.service;

import com.brainics.backend.admin.dto.AdminUserRequest;
import com.brainics.backend.admin.dto.AdminUserResponse;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AdminUserResponse createUser(AdminUserRequest request, Long actorId) {
        log.info("Creating new admin user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(request.getEnabled())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();

        user.addRole(request.getRole());
        AppUser saved = userRepository.save(user);

        auditLogService.logAction(
                actorId,
                "CREATE",
                "USER",
                saved.getId(),
                null,
                null,
                "SUCCESS"
        );

        log.info("Admin user created successfully: {} (ID: {})", saved.getUsername(), saved.getId());
        return toResponse(saved);
    }

    public AdminUserResponse updateUser(Long userId, AdminUserRequest request, Long actorId) {
        log.info("Updating admin user: {}", userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        // Check username uniqueness if changed
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Check email uniqueness if changed
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEnabled(request.getEnabled());

        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update role if changed
        if (!user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()).contains(request.getRole())) {
            user.getRoles().clear();
            user.addRole(request.getRole());
        }

        AppUser updated = userRepository.save(user);

        auditLogService.logAction(
                actorId,
                "UPDATE",
                "USER",
                userId,
                null,
                null,
                "SUCCESS"
        );

        log.info("Admin user updated successfully: {} (ID: {})", updated.getUsername(), userId);
        return toResponse(updated);
    }

    public void deleteUser(Long userId, Long actorId) {
        log.info("Deleting admin user: {}", userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        userRepository.delete(user);

        auditLogService.logAction(
                actorId,
                "DELETE",
                "USER",
                userId,
                null,
                null,
                "SUCCESS"
        );

        log.info("Admin user deleted successfully: {} (ID: {})", user.getUsername(), userId);
    }

    public AdminUserResponse getUserById(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        return toResponse(user);
    }

    public AdminUserResponse getUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        return toResponse(user);
    }

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public Page<AdminUserResponse> getUsersByRole(String role, Pageable pageable) {
        return userRepository.findAllByRolesName(role, pageable)
                .map(this::toResponse);
    }

    public void enableUser(Long userId, Long actorId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        user.setEnabled(true);
        userRepository.save(user);

        auditLogService.logAction(actorId, "UPDATE", "USER", userId, null, null, "SUCCESS");
        log.info("User enabled: {} (ID: {})", user.getUsername(), userId);
    }

    public void disableUser(Long userId, Long actorId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        user.setEnabled(false);
        userRepository.save(user);

        auditLogService.logAction(actorId, "UPDATE", "USER", userId, null, null, "SUCCESS");
        log.info("User disabled: {} (ID: {})", user.getUsername(), userId);
    }

    private AdminUserResponse toResponse(AppUser user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet()))
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().atZone(java.time.ZoneId.of("UTC")) : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().atZone(java.time.ZoneId.of("UTC")) : null)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }
}
