package com.brainics.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileResponse {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String bio;
    private String qualifications;
    private Short experienceYears;
    private String phone;
    private List<SubjectExpertiseResponse> subjectExpertise;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectExpertiseResponse {
        private Long subjectId;
        private String subjectName;
        private String proficiencyLevel;
    }
}
