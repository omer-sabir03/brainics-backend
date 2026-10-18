package com.brainics.backend.admin.controller;

import com.brainics.backend.admin.dto.TeacherProfileRequest;
import com.brainics.backend.admin.dto.TeacherProfileResponse;
import com.brainics.backend.admin.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teachers")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherProfileResponse> createTeacherProfile(
            @PathVariable Long userId,
            @Valid @RequestBody TeacherProfileRequest request,
            Authentication authentication) {
        log.info("Creating teacher profile for user: {}", userId);
        Long actorId = (Long) authentication.getPrincipal();
        TeacherProfileResponse response = teacherService.createTeacherProfile(userId, request, actorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherProfileResponse> updateTeacherProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody TeacherProfileRequest request,
            Authentication authentication) {
        log.info("Updating teacher profile: {}", profileId);
        Long actorId = (Long) authentication.getPrincipal();
        TeacherProfileResponse response = teacherService.updateTeacherProfile(profileId, request, actorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeacherProfile(
            @PathVariable Long profileId,
            Authentication authentication) {
        log.info("Deleting teacher profile: {}", profileId);
        Long actorId = (Long) authentication.getPrincipal();
        teacherService.deleteTeacherProfile(profileId, actorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherProfileResponse> getTeacherProfileById(@PathVariable Long profileId) {
        log.info("Fetching teacher profile: {}", profileId);
        TeacherProfileResponse response = teacherService.getTeacherProfileById(profileId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherProfileResponse> getTeacherProfileByUserId(@PathVariable Long userId) {
        log.info("Fetching teacher profile for user: {}", userId);
        TeacherProfileResponse response = teacherService.getTeacherProfileByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TeacherProfileResponse>> getAllTeacherProfiles(Pageable pageable) {
        log.info("Fetching all teacher profiles: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<TeacherProfileResponse> response = teacherService.getAllTeacherProfiles(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherProfileResponse>> getTeachersBySubject(@PathVariable Long subjectId) {
        log.info("Fetching teachers for subject: {}", subjectId);
        List<TeacherProfileResponse> response = teacherService.getTeachersBySubject(subjectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/proficiency/{proficiencyLevel}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherProfileResponse>> getTeachersByProficiencyLevel(
            @PathVariable String proficiencyLevel) {
        log.info("Fetching teachers by proficiency level: {}", proficiencyLevel);
        List<TeacherProfileResponse> response = teacherService.getTeachersByProficiencyLevel(proficiencyLevel);
        return ResponseEntity.ok(response);
    }
}
