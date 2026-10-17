package com.brainics.backend.auth.service;

import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(normalize(username))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        String[] roles = user.getRoles().stream().map(role -> role.getName().name()).toArray(String[]::new);
        return User.withUsername(user.getUsername()).password(user.getPasswordHash())
                .disabled(!user.isEnabled()).roles(roles).build();
    }

    public static String normalize(String username) { return username.trim().toLowerCase(java.util.Locale.ROOT); }
}
