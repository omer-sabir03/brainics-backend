package com.brainics.backend.academy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassResponse {
    private Long id;
    private Integer classNumber;
    private String description;
    private Boolean isActive;
    private List<SubjectResponse> subjects;
}
