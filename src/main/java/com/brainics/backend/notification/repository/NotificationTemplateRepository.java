package com.brainics.backend.notification.repository;

import com.brainics.backend.notification.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByName(String name);
    Optional<NotificationTemplate> findByNameAndLanguageCode(String name, String languageCode);
    List<NotificationTemplate> findByType(String type);
    List<NotificationTemplate> findByTypeAndIsActiveTrue(String type);
    List<NotificationTemplate> findByLanguageCodeAndIsActiveTrue(String languageCode);
}
