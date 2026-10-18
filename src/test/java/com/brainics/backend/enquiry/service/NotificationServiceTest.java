package com.brainics.backend.enquiry.service;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.academy.model.Subject;
import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.model.EnquirySubject;
import com.brainics.backend.enquiry.repository.EnquirySubjectRepository;
import com.brainics.backend.notification.model.NotificationTemplate;
import com.brainics.backend.notification.repository.NotificationTemplateRepository;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EnquirySubjectRepository enquirySubjectRepository;

    @Mock
    private NotificationTemplateRepository notificationTemplateRepository;

    @Mock
    private Resend resendClient;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(
            enquirySubjectRepository,
            notificationTemplateRepository,
            resendClient
        );

        // Use reflection to set the private fields
        try {
            var adminEmailField = NotificationService.class.getDeclaredField("adminEmail");
            adminEmailField.setAccessible(true);
            adminEmailField.set(notificationService, "admin@brainics.com");

            var fromEmailField = NotificationService.class.getDeclaredField("fromEmail");
            fromEmailField.setAccessible(true);
            fromEmailField.set(notificationService, "noreply@brainics.com");

            var frontendUrlField = NotificationService.class.getDeclaredField("frontendUrl");
            frontendUrlField.setAccessible(true);
            frontendUrlField.set(notificationService, "https://brainics.local");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testTemplateVariableReplacement() {
        String template = "Dear {{parentName}}, your enquiry for {{studentName}} has been received.";
        java.util.Map<String, String> variables = java.util.Map.of(
            "parentName", "Ahmed Ali",
            "studentName", "Ali Ahmed"
        );

        // Test via reflection since method is private
        try {
            var method = NotificationService.class.getDeclaredMethod("renderTemplate", String.class, java.util.Map.class);
            method.setAccessible(true);
            String result = (String) method.invoke(notificationService, template, variables);

            assertEquals("Dear Ahmed Ali, your enquiry for Ali Ahmed has been received.", result);
        } catch (Exception e) {
            fail("Template rendering failed: " + e.getMessage());
        }
    }

    @Test
    void testTemplateVariableReplacementWithMissingVariable() {
        String template = "Hello {{name}}, your status is {{status}}.";
        java.util.Map<String, String> variables = java.util.Map.of("name", "John");

        try {
            var method = NotificationService.class.getDeclaredMethod("renderTemplate", String.class, java.util.Map.class);
            method.setAccessible(true);
            String result = (String) method.invoke(notificationService, template, variables);

            assertEquals("Hello John, your status is .", result);
        } catch (Exception e) {
            fail("Template rendering failed: " + e.getMessage());
        }
    }

    @Test
    void testAdminNotificationVariablesBuilding() {
        Class classEntity = Class.builder().classNumber(5).build();
        classEntity.setId(1L);

        Subject subject = Subject.builder().nameEn("Math").nameUrdu("ریاضی").build();
        subject.setId(1L);

        AdmissionEnquiry enquiry = AdmissionEnquiry.builder()
            .id(100L)
            .studentName("Ali")
            .classEntity(classEntity)
            .parentName("Ahmed")
            .parentEmail("ahmed@example.com")
            .parentPhone("+923001234567")
            .createdAt(ZonedDateTime.now())
            .build();

        EnquirySubject enquirySubject = EnquirySubject.builder()
            .enquiry(enquiry)
            .subject(subject)
            .build();

        when(enquirySubjectRepository.findByEnquiry(enquiry))
            .thenReturn(Arrays.asList(enquirySubject));

        try {
            var method = NotificationService.class.getDeclaredMethod("buildAdminNotificationVariables", AdmissionEnquiry.class);
            method.setAccessible(true);
            java.util.Map<String, String> variables = (java.util.Map<String, String>) method.invoke(notificationService, enquiry);

            assertEquals("Ali", variables.get("studentName"));
            assertEquals("5", variables.get("classLevel"));
            assertEquals("Ahmed", variables.get("parentName"));
            assertEquals("ahmed@example.com", variables.get("parentEmail"));
            assertTrue(variables.get("dashboardUrl").contains("/admin/enquiries/100"));
        } catch (Exception e) {
            fail("Variable building failed: " + e.getMessage());
        }
    }

    @Test
    void testEmailSendingSuccess() throws ResendException {
        var emailsEndpoint = mock(com.resend.services.emails.EmailEndpoint.class);
        when(resendClient.emails()).thenReturn(emailsEndpoint);

        try {
            var sendMethod = NotificationService.class.getDeclaredMethod("sendEmail", String.class, String.class, String.class);
            sendMethod.setAccessible(true);
            sendMethod.invoke(notificationService, "test@example.com", "Test Subject", "<p>Test Body</p>");

            verify(emailsEndpoint, times(1)).send(any());
        } catch (Exception e) {
            fail("Email sending test failed: " + e.getMessage());
        }
    }
}
