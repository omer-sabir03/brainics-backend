package com.brainics.backend.notification.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_notification_templates_name", columnList = "name"),
    @Index(name = "idx_notification_templates_type", columnList = "type"),
    @Index(name = "idx_notification_templates_language", columnList = "language_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "language_code", nullable = false, length = 5)
    @Builder.Default
    private String languageCode = "en";

    @Column(name = "subject", length = 255)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Type(JsonBinaryType.class)
    @Column(name = "variables", columnDefinition = "jsonb")
    private List<String> variables;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
        if (languageCode == null) {
            languageCode = "en";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
