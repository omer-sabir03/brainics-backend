package com.brainics.backend.admin.model;

import com.brainics.backend.auth.model.AppUser;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import java.time.ZonedDateTime;
import java.util.Map;

@Entity
@Table(name = "admin_audit_logs", indexes = {
    @Index(name = "idx_admin_audit_logs_actor_id", columnList = "actor_id"),
    @Index(name = "idx_admin_audit_logs_action", columnList = "action"),
    @Index(name = "idx_admin_audit_logs_resource_type", columnList = "resource_type"),
    @Index(name = "idx_admin_audit_logs_created_at", columnList = "created_at DESC"),
    @Index(name = "idx_admin_audit_logs_resource_id", columnList = "resource_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private AppUser actor;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "resource_name", length = 255)
    private String resourceName;

    @Type(JsonBinaryType.class)
    @Column(name = "old_values", columnDefinition = "jsonb")
    private Map<String, Object> oldValues;

    @Type(JsonBinaryType.class)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "SUCCESS";

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
}
