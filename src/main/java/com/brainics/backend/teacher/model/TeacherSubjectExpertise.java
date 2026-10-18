package com.brainics.backend.teacher.model;

import com.brainics.backend.academy.model.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Entity
@Table(name = "teacher_subject_expertise", indexes = {
    @Index(name = "idx_teacher_subject_expertise_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_teacher_subject_expertise_subject_id", columnList = "subject_id"),
    @Index(name = "uq_teacher_subject_expertise", columnList = "teacher_id, subject_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherProfile teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "proficiency_level", length = 20)
    @Builder.Default
    private String proficiencyLevel = "INTERMEDIATE";

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
}
