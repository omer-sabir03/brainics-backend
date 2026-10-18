package com.brainics.backend.enquiry.model;

import com.brainics.backend.academy.model.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Entity
@Table(name = "enquiry_subjects", indexes = {
    @Index(name = "idx_enquiry_subjects_enquiry_id", columnList = "enquiry_id"),
    @Index(name = "idx_enquiry_subjects_subject_id", columnList = "subject_id"),
    @Index(name = "uq_enquiry_subjects_unique", columnList = "enquiry_id, subject_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquirySubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enquiry_id", nullable = false)
    private AdmissionEnquiry enquiry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
}
