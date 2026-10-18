package com.brainics.backend.academy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponse {
    private Long id;
    private String nameEn;
    private String nameUrdu;
    private String description;
    private Boolean isActive;

    public String getName(String language) {
        return "ur".equalsIgnoreCase(language) ? nameUrdu : nameEn;
    }
}
