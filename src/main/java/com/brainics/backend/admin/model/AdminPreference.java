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
@Table(name = "admin_preferences", indexes = {
    @Index(name = "idx_admin_preferences_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "dashboard_layout", length = 50)
    @Builder.Default
    private String dashboardLayout = "DEFAULT";

    @Column(name = "preferred_metrics")
    private String[] preferredMetrics;

    @Type(JsonBinaryType.class)
    @Column(name = "notification_settings", columnDefinition = "jsonb")
    private Map<String, Object> notificationSettings;

    @Column(name = "theme", length = 20)
    @Builder.Default
    private String theme = "LIGHT";

    @Column(name = "rows_per_page")
    @Builder.Default
    private Integer rowsPerPage = 20;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
