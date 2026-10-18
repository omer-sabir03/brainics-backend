package com.brainics.backend.enquiry.dto;

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
public class EnquiryResponse {
    private Long id;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String studentName;
    private Long classId;
    private String className;
    private List<SubjectPreferenceResponse> preferredSubjects;
    private String message;
    private String status;
    private String source;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectPreferenceResponse {
        private Long subjectId;
        private String subjectNameEn;
        private String subjectNameUrdu;
    }
}
