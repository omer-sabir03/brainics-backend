package com.brainics.backend.notification.service;

import com.brainics.backend.notification.model.NotificationTemplate;
import com.brainics.backend.notification.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationTemplateInitializer {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeTemplates() {
        log.info("Initializing notification templates...");

        List<String> requiredTemplates = Arrays.asList(
            "enquiry_received_admin",
            "enquiry_received_parent",
            "enquiry_received_admin_ur",
            "enquiry_received_parent_ur"
        );

        for (String templateName : requiredTemplates) {
            boolean exists = notificationTemplateRepository.findByName(templateName).isPresent();
            if (!exists) {
                log.warn("Missing notification template: {}. Please check database migration V8.", templateName);
            } else {
                log.debug("Notification template verified: {}", templateName);
            }
        }

        log.info("Notification template initialization completed");
    }
}
