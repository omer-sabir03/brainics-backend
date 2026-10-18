package com.brainics.backend.admin.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileRequest {

    @NotBlank(message = "Bio is required")
    @Size(min = 10, max = 1000, message = "Bio must be between 10 and 1000 characters")
    private String bio;

    @NotBlank(message = "Qualifications are required")
    @Size(min = 5, max = 500, message = "Qualifications must be between 5 and 500 characters")
    private String qualifications;

    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 70, message = "Experience years cannot exceed 70")
    private Short experienceYears;

    @Pattern(regexp = "^(\\+92|0)[0-9]{10}$", message = "Invalid phone format")
    private String phone;

    @NotEmpty(message = "At least one subject expertise is required")
    private List<SubjectExpertiseRequest> subjectExpertise;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectExpertiseRequest {
        @NotNull(message = "Subject ID is required")
        private Long subjectId;

        @NotBlank(message = "Proficiency level is required")
        @Pattern(regexp = "BEGINNER|INTERMEDIATE|ADVANCED|EXPERT", message = "Invalid proficiency level")
        private String proficiencyLevel;
    }
}
