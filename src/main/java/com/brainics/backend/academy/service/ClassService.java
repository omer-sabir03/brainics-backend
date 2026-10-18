package com.brainics.backend.academy.service;

import com.brainics.backend.academy.dto.ClassResponse;
import com.brainics.backend.academy.dto.SubjectResponse;
import com.brainics.backend.academy.model.Class;
import com.brainics.backend.academy.model.ClassSubject;
import com.brainics.backend.academy.repository.ClassRepository;
import com.brainics.backend.academy.repository.ClassSubjectRepository;
import com.brainics.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassSubjectRepository classSubjectRepository;

    public List<ClassResponse> getAllClasses() {
        return classRepository.findAllByIsActiveTrueOrderByClassNumber().stream()
            .map(this::toClassResponse)
            .collect(Collectors.toList());
    }

    public ClassResponse getClassById(Long classId) {
        Class classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
        return toClassResponse(classEntity);
    }

    public Class getClassByIdOrThrow(Long classId) {
        return classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
    }

    public ClassResponse getClassByNumber(Short classNumber) {
        Class classEntity = classRepository.findByClassNumber(classNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found with number: " + classNumber));
        return toClassResponse(classEntity);
    }

    private ClassResponse toClassResponse(Class classEntity) {
        List<ClassSubject> classSubjects = classSubjectRepository.findByClassEntityAndIsAvailableTrue(classEntity);

        List<SubjectResponse> subjects = classSubjects.stream()
            .map(cs -> SubjectResponse.builder()
                .id(cs.getSubject().getId())
                .nameEn(cs.getSubject().getNameEn())
                .nameUrdu(cs.getSubject().getNameUrdu())
                .description(cs.getSubject().getDescription())
                .isActive(cs.getSubject().getIsActive())
                .build())
            .collect(Collectors.toList());

        return ClassResponse.builder()
            .id(classEntity.getId())
            .classNumber(classEntity.getClassNumber())
            .description(classEntity.getDescription())
            .isActive(classEntity.getIsActive())
            .subjects(subjects)
            .build();
    }
}
