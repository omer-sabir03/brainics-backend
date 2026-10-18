package com.brainics.backend.email;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ResendEmailService {

    private final RestClient restClient;
    private final String from;
    private final String replyTo;

    public ResendEmailService(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from}") String from,
            @Value("${resend.reply-to:brainics@outlook.com}")
            String replyTo) {

        this.from = from;
        this.replyTo = replyTo;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader(
                        "Content-Type",
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void sendEmail(String to, String subject, String html) {
        Map<String, Object> request = Map.of(
                "from", from,
                "to", List.of(to),
                "subject", subject,
                "html", html,
                "reply_to", replyTo
        );

        restClient.post()
                .uri("/emails")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendVerificationEmail(
            String to, String verificationUrl) {

        String html = "<h2>Welcome to Brainics!</h2>" +
                "<p>Please verify your email address by clicking below.</p>" +
                "<p><a href=\"" + verificationUrl + "\">Verify my email</a></p>" +
                "<p>If you did not create an account, ignore this email.</p>";

        sendEmail(to, "Verify your Brainics email", html);
    }

    public void sendPasswordResetEmail(
            String to, String resetUrl) {

        String html = "<h2>Reset your Brainics password</h2>" +
                "<p>Click below to reset your password.</p>" +
                "<p><a href=\"" + resetUrl + "\">Reset password</a></p>" +
                "<p>If you did not request this, ignore this email.</p>";

        sendEmail(to, "Reset your Brainics password", html);
    }
}