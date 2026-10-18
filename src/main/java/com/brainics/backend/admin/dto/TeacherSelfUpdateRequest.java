package com.brainics.backend.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

// Fields a teacher may change on their own profile (subjects are admin-managed)
public record TeacherSelfUpdateRequest(
        @Size(max = 2000) String bio,
        @Size(max = 2000) String qualifications,
        @Min(0) @Max(70) Short experienceYears,
        @Size(max = 20) String phone
) {
}
