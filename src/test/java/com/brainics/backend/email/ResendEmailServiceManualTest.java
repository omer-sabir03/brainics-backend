package com.brainics.backend.email;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ResendEmailServiceManualTest {

    @Test
    void shouldSendTestEmail() {
        String apiKey = System.getenv("RESEND_API_KEY");
        String from = System.getenv("BRAINICS_EMAIL_FROM");
        String replyTo = System.getenv("BRAINICS_EMAIL_REPLY_TO");

        if (apiKey == null || from == null) {
            throw new IllegalStateException(
                    "RESEND_API_KEY and BRAINICS_EMAIL_FROM must be set");
        }

        ResendEmailService service =
                new ResendEmailService(apiKey, from, replyTo);

        assertDoesNotThrow(() -> service.sendEmail(
                "brainics@outlook.com",
                "Brainics Email Integration Test",
                "<h2>Email integration works!</h2>"
                        + "<p>This is a test email from Brainics.</p>"
        ));
    }
}