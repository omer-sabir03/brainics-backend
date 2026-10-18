package com.brainics.backend.enquiry.model;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.auth.model.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Entity
@Table(name = "admission_enquiries", indexes = {
    @Index(name = "idx_admission_enquiries_parent_email", columnList = "parent_email"),
    @Index(name = "idx_admission_enquiries_parent_phone", columnList = "parent_phone"),
    @Index(name = "idx_admission_enquiries_status", columnList = "status"),
    @Index(name = "idx_admission_enquiries_class_id", columnList = "class_id"),
    @Index(name = "idx_admission_enquiries_created_at", columnList = "created_at DESC"),
    @Index(name = "idx_admission_enquiries_assigned_to", columnList = "assigned_to_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdmissionEnquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_name", nullable = false, length = 100)
    private String parentName;

    @Column(name = "parent_email", nullable = false, length = 254)
    private String parentEmail;

    @Column(name = "parent_phone", nullable = false, length = 20)
    private String parentPhone;

    @Column(name = "student_name", nullable = false, length = 100)
    private String studentName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classEntity;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "NEW";

    @Column(name = "source", nullable = false, length = 30)
    @Builder.Default
    private String source = "WEB";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private AppUser assignedTo;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime updatedAt;

    @Column(name = "created_ip_address", length = 45)
    private String createdIpAddress;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
        if (status == null) {
            status = "NEW";
        }
        if (source == null) {
            source = "WEB";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}
