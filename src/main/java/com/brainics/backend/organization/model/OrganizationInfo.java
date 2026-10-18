package com.brainics.backend.organization.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

@Entity
@Table(name = "organization_info", indexes = {
    @Index(name = "idx_organization_info_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone_primary", nullable = false, length = 20)
    private String phonePrimary;

    @Column(name = "phone_secondary", length = 20)
    private String phoneSecondary;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 100)
    @Builder.Default
    private String country = "Pakistan";

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Type(JsonBinaryType.class)
    @Column(name = "social_media_links", columnDefinition = "jsonb")
    private Map<String, String> socialMediaLinks;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Type(JsonBinaryType.class)
    @Column(name = "office_hours", columnDefinition = "jsonb")
    private Map<String, Map<String, String>> officeHours;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
        if (country == null) {
            country = "Pakistan";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
