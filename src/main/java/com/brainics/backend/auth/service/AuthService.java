package com.brainics.backend.auth.service;

import com.brainics.backend.auth.dto.AuthResponse;
import com.brainics.backend.auth.dto.UserResponse;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final long expirationSeconds;

    public AuthService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder,
                       UserRepository userRepository,
                       @Value("${app.jwt.access-token-minutes:15}") long expirationMinutes) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.expirationSeconds = Math.multiplyExact(expirationMinutes, 60);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(CustomUserDetailsService.normalize(username), password));
        AppUser user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
        Instant now = Instant.now();
        List<String> roles = user.getRoles().stream().map(role -> role.getName().name()).sorted().toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("brainics-backend")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .claim("roles", roles)
                .build();
//        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder
                .encode(JwtEncoderParameters.from(headers, claims))
                .getTokenValue();
        return new AuthResponse(token, "Bearer", expirationSeconds, UserResponse.from(user));
    }
}
