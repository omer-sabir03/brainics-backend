package com.brainics.backend.auth.service;

import com.brainics.backend.auth.dto.CreateUserRequest;
import com.brainics.backend.auth.dto.UserResponse;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.model.Role;
import com.brainics.backend.auth.model.RoleName;
import com.brainics.backend.auth.repository.RoleRepository;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.common.exception.BadRequestException;
import com.brainics.backend.common.exception.ConflictException;
import com.brainics.backend.common.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.List;

@Service
public class UserManagementService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (request.role() == RoleName.ADMIN) {
            throw new BadRequestException("Admin accounts cannot be created through this endpoint");
        }
        String username = CustomUserDetailsService.normalize(request.username());
        if (userRepository.existsByUsername(username)) throw new ConflictException("Username already exists");
        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new IllegalStateException("Required role is missing from database"));
        AppUser user = new AppUser(username, passwordEncoder.encode(request.password()), Set.of(role));
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse resetPassword(long id, String newPassword) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN)) {
            throw new BadRequestException("Admin passwords cannot be reset through this endpoint");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse setEnabled(long id, boolean enabled) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN) && !enabled) {
            throw new BadRequestException("Admin accounts cannot be disabled through this endpoint");
        }
        user.setEnabled(enabled);
        return UserResponse.from(user);
    }
}
