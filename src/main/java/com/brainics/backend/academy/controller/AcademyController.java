package com.brainics.backend.academy.controller;

import com.brainics.backend.academy.dto.ClassResponse;
import com.brainics.backend.academy.dto.SubjectResponse;
import com.brainics.backend.academy.service.ClassService;
import com.brainics.backend.academy.service.SubjectService;
import com.brainics.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/academy")
@RequiredArgsConstructor
public class AcademyController {

    private final ClassService classService;
    private final SubjectService subjectService;

    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getAllClasses() {
        List<ClassResponse> classes = classService.getAllClasses();
        return ResponseEntity.ok(ApiResponse.success(classes));
    }

    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<ClassResponse>> getClassById(@PathVariable Long classId) {
        ClassResponse classResponse = classService.getClassById(classId);
        return ResponseEntity.ok(ApiResponse.success(classResponse));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAllSubjects() {
        List<SubjectResponse> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.success(subjects));
    }

    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectById(@PathVariable Long subjectId) {
        SubjectResponse subject = subjectService.getSubjectById(subjectId);
        return ResponseEntity.ok(ApiResponse.success(subject));
    }
}
