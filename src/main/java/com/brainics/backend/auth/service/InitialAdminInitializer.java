package com.brainics.backend.auth.service;

import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.model.Role;
import com.brainics.backend.auth.model.RoleName;
import com.brainics.backend.auth.repository.RoleRepository;
import com.brainics.backend.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class InitialAdminInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(InitialAdminInitializer.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public InitialAdminInitializer(UserRepository userRepository, RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${app.bootstrap-admin.username:}") String username,
                                   @Value("${app.bootstrap-admin.password:}") String password) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (username.isBlank() && password.isBlank()) {
            log.warn("No initial admin configured. Set INITIAL_ADMIN_USERNAME and INITIAL_ADMIN_PASSWORD to bootstrap the first admin.");
            return;
        }
        if (username.isBlank() || password.isBlank() || password.length() < 12) {
            throw new IllegalStateException("Configure both INITIAL_ADMIN_USERNAME and INITIAL_ADMIN_PASSWORD (at least 12 characters)");
        }
        // Bootstrap only once: if any admin exists, never create another one from configuration
        if (userRepository.existsByRolesName(RoleName.ADMIN)) {
            log.info("An admin account already exists; initial admin is not created.");
            return;
        }
        String normalized = CustomUserDetailsService.normalize(username);
        if (userRepository.existsByUsername(normalized)) {
            log.warn("Configured initial admin username is already used by a non-admin account; no changes made.");
            return;
        }
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing; verify Flyway V2 migration"));
        // The password is hashed by the configured encoder and never logged
        userRepository.save(new AppUser(normalized, passwordEncoder.encode(password), Set.of(adminRole)));
        log.info("Initial admin account created from environment configuration.");
    }
}
