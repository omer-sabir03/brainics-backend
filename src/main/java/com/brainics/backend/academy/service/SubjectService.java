package com.brainics.backend.academy.service;

import com.brainics.backend.academy.dto.SubjectResponse;
import com.brainics.backend.academy.model.Subject;
import com.brainics.backend.academy.repository.SubjectRepository;
import com.brainics.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAllByIsActiveTrueOrderByNameEn().stream()
            .map(this::toSubjectResponse)
            .collect(Collectors.toList());
    }

    public SubjectResponse getSubjectById(Long subjectId) {
        Subject subject = getSubjectByIdOrThrow(subjectId);
        return toSubjectResponse(subject);
    }

    public Subject getSubjectByIdOrThrow(Long subjectId) {
        return subjectRepository.findById(subjectId)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));
    }

    public List<SubjectResponse> getSubjectsByIds(List<Long> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return List.of();
        }

        return subjectRepository.findAllById(subjectIds).stream()
            .map(this::toSubjectResponse)
            .collect(Collectors.toList());
    }

    private SubjectResponse toSubjectResponse(Subject subject) {
        return SubjectResponse.builder()
            .id(subject.getId())
            .nameEn(subject.getNameEn())
            .nameUrdu(subject.getNameUrdu())
            .description(subject.getDescription())
            .isActive(subject.getIsActive())
            .build();
    }
}
