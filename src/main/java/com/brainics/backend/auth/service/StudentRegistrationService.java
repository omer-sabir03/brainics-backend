package com.brainics.backend.auth.service;

import com.brainics.backend.auth.dto.StudentRegistrationRequest;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.model.EmailVerificationToken;
import com.brainics.backend.auth.model.Role;
import com.brainics.backend.auth.model.RoleName;
import com.brainics.backend.auth.repository.EmailVerificationTokenRepository;
import com.brainics.backend.auth.repository.RoleRepository;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.common.exception.BadRequestException;
import com.brainics.backend.common.exception.ConflictException;
import com.brainics.backend.email.ResendEmailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Set;

@Service
public class StudentRegistrationService {

    private static final Duration TOKEN_LIFETIME =
            Duration.ofHours(24);

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResendEmailService emailService;
    private final String frontendUrl;

    public StudentRegistrationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            EmailVerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            ResendEmailService emailService,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    @Transactional
    public void register(StudentRegistrationRequest request) {

        String username =
                CustomUserDetailsService.normalize(request.username());

        String email =
                request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "An account with this email already exists"
            );
        }

        Role studentRole = roleRepository
                .findByName(RoleName.STUDENT)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "STUDENT role is missing from database"
                        )
                );

        AppUser user = new AppUser(
                username,
                passwordEncoder.encode(request.password()),
                Set.of(studentRole)
        );

        user.setEmail(email);
        user.setEmailVerified(false);
        user.setEnabled(false);

        user = userRepository.save(user);

        String rawToken = createRawToken();

        tokenRepository.deleteByUser_Id(user.getId());

        EmailVerificationToken verificationToken =
                new EmailVerificationToken(
                        user,
                        sha256(rawToken),
                        Instant.now().plus(TOKEN_LIFETIME)
                );

        tokenRepository.save(verificationToken);

        String verificationUrl =
                frontendUrl + "/verify-email?token=" + rawToken;

        emailService.sendVerificationEmail(
                email,
                verificationUrl
        );
    }

    @Transactional
    public void verifyEmail(String rawToken) {

        if (rawToken == null
                || rawToken.isBlank()
                || rawToken.length() > 200) {

            throw new BadRequestException(
                    "Verification link is invalid or expired"
            );
        }

        String tokenHash = sha256(rawToken);

        EmailVerificationToken token =
                tokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Verification link is invalid or expired"
                                )
                        );

        if (!token.getExpiresAt().isAfter(Instant.now())) {
            throw new BadRequestException(
                    "Verification link is invalid or expired"
            );
        }

        AppUser user = token.getUser();

        user.setEmailVerified(true);
        user.setEnabled(true);

        tokenRepository.delete(token);
        userRepository.save(user);
    }

    private static String createRawToken() {

        byte[] bytes = new byte[32];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private static String sha256(String value) {

        try {
            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));

            return java.util.HexFormat
                    .of()
                    .formatHex(digest);

        } catch (NoSuchAlgorithmException ex) {

            throw new IllegalStateException(
                    "SHA-256 is unavailable",
                    ex
            );
        }
    }
}