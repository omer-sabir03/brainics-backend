package com.brainics.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private Boolean enabled;
    private Boolean emailVerified;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String firstName;
    private String lastName;
    private String phone;
}
