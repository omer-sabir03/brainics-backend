package com.brainics.backend.enquiry.service;

import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.model.EnquirySubject;
import com.brainics.backend.email.ResendEmailService;
import com.brainics.backend.enquiry.repository.EnquirySubjectRepository;
import com.brainics.backend.notification.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final EnquirySubjectRepository enquirySubjectRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final ResendEmailService resendEmailService;

    @Value("${app.enquiry.admin-notification-email}")
    private String adminEmail;

    @Value("${brainics.frontend-url:https://brainics.vercel.app}")
    private String frontendUrl;

    public void sendEnquiryNotifications(AdmissionEnquiry enquiry) {
        sendAdminNotification(enquiry);
        sendParentNotification(enquiry);
    }

    private void sendAdminNotification(AdmissionEnquiry enquiry) {
        try {
            var template = notificationTemplateRepository.findByNameAndLanguageCode("enquiry_received_admin", "en")
                .orElse(null);

            if (template == null) {
                log.warn("Admin notification template not found");
                return;
            }

            Map<String, String> variables = buildAdminNotificationVariables(enquiry);
            String emailBody = renderTemplate(template.getBody(), variables);
            String subject = renderTemplate(template.getSubject(), variables);

            sendEmail(adminEmail, subject, emailBody);
            log.info("Admin notification sent for enquiry: {}", enquiry.getId());
        } catch (Exception e) {
            log.error("Failed to send admin notification for enquiry: {}", enquiry.getId(), e);
        }
    }

    @Async
    private void sendParentNotification(AdmissionEnquiry enquiry) {
        try {
            var template = notificationTemplateRepository.findByNameAndLanguageCode("enquiry_received_parent", "en")
                .orElse(null);

            if (template == null) {
                log.warn("Parent notification template not found");
                return;
            }

            Map<String, String> variables = buildParentNotificationVariables(enquiry);
            String emailBody = renderTemplate(template.getBody(), variables);
            String subject = renderTemplate(template.getSubject(), variables);

            sendEmail(enquiry.getParentEmail(), subject, emailBody);
            log.info("Parent notification sent for enquiry: {}", enquiry.getId());
        } catch (Exception e) {
            log.error("Failed to send parent notification for enquiry: {}", enquiry.getId(), e);
        }
    }

    private Map<String, String> buildAdminNotificationVariables(AdmissionEnquiry enquiry) {
        Map<String, String> vars = new HashMap<>();
        vars.put("studentName", enquiry.getStudentName());
        vars.put("classLevel", String.valueOf(enquiry.getClassEntity().getClassNumber()));
        vars.put("parentName", enquiry.getParentName());
        vars.put("parentEmail", enquiry.getParentEmail());
        vars.put("parentPhone", enquiry.getParentPhone());

        List<String> subjects = enquirySubjectRepository.findByEnquiry(enquiry).stream()
            .map(es -> es.getSubject().getNameEn())
            .collect(Collectors.toList());
        vars.put("subjects", String.join(", ", subjects));

        vars.put("submittedAt", enquiry.getCreatedAt().toString());
        vars.put("dashboardUrl", frontendUrl + "/admin/enquiries/" + enquiry.getId());

        return vars;
    }

    private Map<String, String> buildParentNotificationVariables(AdmissionEnquiry enquiry) {
        Map<String, String> vars = new HashMap<>();
        vars.put("parentName", enquiry.getParentName());
        vars.put("studentName", enquiry.getStudentName());
        vars.put("classLevel", String.valueOf(enquiry.getClassEntity().getClassNumber()));
        vars.put("enquiryId", String.valueOf(enquiry.getId()));
        vars.put("parentPhone", enquiry.getParentPhone());
        vars.put("parentEmail", enquiry.getParentEmail());

        List<String> subjects = enquirySubjectRepository.findByEnquiry(enquiry).stream()
            .map(es -> es.getSubject().getNameEn())
            .collect(Collectors.toList());
        vars.put("subjects", String.join(", ", subjects));

        vars.put("statusUrl", frontendUrl + "/enquiry-status/" + enquiry.getId());

        return vars;
    }

    private String renderTemplate(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue() != null ? entry.getValue() : "");
        }
        return result.replaceAll("\\{\\{[^}]*\\}\\}", "");
    }

    private void sendEmail(String recipientEmail, String subject, String htmlBody) {
        resendEmailService.sendEmail(recipientEmail, subject, htmlBody);
    }
}
