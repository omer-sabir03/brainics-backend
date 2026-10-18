package com.brainics.backend.admin.service;

import com.brainics.backend.admin.dto.AdminUserRequest;
import com.brainics.backend.admin.dto.AdminUserResponse;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.model.Role;
import com.brainics.backend.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AdminUserService adminUserService;

    private AdminUserRequest validRequest;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        validRequest = AdminUserRequest.builder()
                .username("testadmin")
                .email("admin@test.com")
                .password("SecurePass123")
                .role("ADMIN")
                .firstName("Test")
                .lastName("Admin")
                .phone("+923001234567")
                .enabled(true)
                .build();

        testUser = AppUser.builder()
                .id(1L)
                .username("testadmin")
                .email("admin@test.com")
                .firstName("Test")
                .lastName("Admin")
                .phone("+923001234567")
                .enabled(true)
                .build();
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(AppUser.class))).thenReturn(testUser);

        AdminUserResponse response = adminUserService.createUser(validRequest, 1L);

        assertNotNull(response);
        assertEquals("testadmin", response.getUsername());
        assertEquals("admin@test.com", response.getEmail());
        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(auditLogService, times(1)).logAction(anyLong(), eq("CREATE"), eq("USER"), anyLong(), any(), any(), eq("SUCCESS"));
    }

    @Test
    void testCreateUserDuplicateUsername() {
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminUserService.createUser(validRequest, 1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUserDuplicateEmail() {
        when(userRepository.existsByUsername(validRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminUserService.createUser(validRequest, 1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newtestadmin")).thenReturn(false);
        when(userRepository.existsByEmail("newadmin@test.com")).thenReturn(false);
        when(userRepository.save(any(AppUser.class))).thenReturn(testUser);

        AdminUserRequest updateRequest = AdminUserRequest.builder()
                .username("newtestadmin")
                .email("newadmin@test.com")
                .password("NewPass123")
                .role("ADMIN")
                .firstName("Updated")
                .lastName("Admin")
                .phone("+923009876543")
                .enabled(true)
                .build();

        AdminUserResponse response = adminUserService.updateUser(1L, updateRequest, 1L);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(AppUser.class));
        verify(auditLogService, times(1)).logAction(anyLong(), eq("UPDATE"), eq("USER"), eq(1L), any(), any(), eq("SUCCESS"));
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        adminUserService.deleteUser(1L, 1L);

        verify(userRepository, times(1)).delete(testUser);
        verify(auditLogService, times(1)).logAction(anyLong(), eq("DELETE"), eq("USER"), eq(1L), any(), any(), eq("SUCCESS"));
    }

    @Test
    void testEnableUserSuccess() {
        testUser.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(AppUser.class))).thenReturn(testUser);

        adminUserService.enableUser(1L, 1L);

        verify(userRepository, times(1)).save(testUser);
        verify(auditLogService, times(1)).logAction(anyLong(), eq("UPDATE"), eq("USER"), eq(1L), any(), any(), eq("SUCCESS"));
    }

    @Test
    void testDisableUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(AppUser.class))).thenReturn(testUser);

        adminUserService.disableUser(1L, 1L);

        verify(userRepository, times(1)).save(testUser);
        verify(auditLogService, times(1)).logAction(anyLong(), eq("UPDATE"), eq("USER"), eq(1L), any(), any(), eq("SUCCESS"));
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> adminUserService.getUserById(999L));
    }
}
