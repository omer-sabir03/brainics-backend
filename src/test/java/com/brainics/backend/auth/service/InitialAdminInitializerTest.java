package com.brainics.backend.auth.service;

import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.model.Role;
import com.brainics.backend.auth.model.RoleName;
import com.brainics.backend.auth.repository.RoleRepository;
import com.brainics.backend.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialAdminInitializerTest {

    private static final String PASSWORD = "a-long-test-password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments args;

    @Test
    void createsAdminWithHashedPasswordWhenNoAdminExists() throws Exception {
        InitialAdminInitializer initializer = new InitialAdminInitializer(
                userRepository, roleRepository, passwordEncoder, "Owner", PASSWORD);
        when(userRepository.existsByRolesName(RoleName.ADMIN)).thenReturn(false);
        when(userRepository.existsByUsername("owner")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(new Role(RoleName.ADMIN)));
        when(passwordEncoder.encode(PASSWORD)).thenReturn("hashed-value");

        initializer.run(args);

        ArgumentCaptor<AppUser> saved = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(saved.capture());
        assertEquals("owner", saved.getValue().getUsername());
        assertEquals("hashed-value", saved.getValue().getPasswordHash());
        assertNotEquals(PASSWORD, saved.getValue().getPasswordHash());
    }

    @Test
    void doesNothingWhenAnAdminAlreadyExists() throws Exception {
        InitialAdminInitializer initializer = new InitialAdminInitializer(
                userRepository, roleRepository, passwordEncoder, "Owner", PASSWORD);
        when(userRepository.existsByRolesName(RoleName.ADMIN)).thenReturn(true);

        initializer.run(args);

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void doesNotCreateWhenUsernameBelongsToANonAdmin() throws Exception {
        InitialAdminInitializer initializer = new InitialAdminInitializer(
                userRepository, roleRepository, passwordEncoder, "teacher1", PASSWORD);
        when(userRepository.existsByRolesName(RoleName.ADMIN)).thenReturn(false);
        when(userRepository.existsByUsername("teacher1")).thenReturn(true);

        initializer.run(args);

        verify(userRepository, never()).save(any());
    }
}
