package com.brainics.backend.auth.dto;

import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.model.RoleName;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(Long id, String username, Set<RoleName> roles, boolean enabled, Instant createdAt) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toUnmodifiableSet()),
                user.isEnabled(), user.getCreatedAt());
    }
}
