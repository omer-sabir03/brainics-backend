package com.brainics.backend.admin.service;

import com.brainics.backend.admin.dto.TeacherProfileRequest;
import com.brainics.backend.admin.dto.TeacherProfileResponse;
import com.brainics.backend.teacher.model.TeacherProfile;
import com.brainics.backend.teacher.model.TeacherSubjectExpertise;
import com.brainics.backend.teacher.repository.TeacherProfileRepository;
import com.brainics.backend.teacher.repository.TeacherSubjectExpertiseRepository;
import com.brainics.backend.academy.model.Subject;
import com.brainics.backend.academy.repository.SubjectRepository;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeacherService {

    private final TeacherProfileRepository teacherProfileRepository;
    private final TeacherSubjectExpertiseRepository subjectExpertiseRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AuditLogService auditLogService;

    public TeacherProfileResponse createTeacherProfile(Long userId, TeacherProfileRequest request, Long actorId) {
        log.info("Creating teacher profile for user: {}", userId);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        if (teacherProfileRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Teacher profile already exists for user: " + userId);
        }

        TeacherProfile profile = TeacherProfile.builder()
                .user(user)
                .bio(request.getBio())
                .qualifications(request.getQualifications())
                .experienceYears(request.getExperienceYears())
                .phone(request.getPhone())
                .build();

        TeacherProfile saved = teacherProfileRepository.save(profile);

        // Add subject expertise
        for (TeacherProfileRequest.SubjectExpertiseRequest expertiseRequest : request.getSubjectExpertise()) {
            Subject subject = subjectRepository.findById(expertiseRequest.getSubjectId())
                    .orElseThrow(() -> new NoSuchElementException("Subject not found: " + expertiseRequest.getSubjectId()));

            TeacherSubjectExpertise expertise = TeacherSubjectExpertise.builder()
                    .teacher(saved)
                    .subject(subject)
                    .proficiencyLevel(expertiseRequest.getProficiencyLevel())
                    .build();

            subjectExpertiseRepository.save(expertise);
        }

        auditLogService.logAction(
                actorId,
                "CREATE",
                "TEACHER_PROFILE",
                saved.getId(),
                null,
                null,
                "SUCCESS"
        );

        log.info("Teacher profile created successfully: {} (ID: {})", user.getUsername(), saved.getId());
        return toResponse(saved);
    }

    public TeacherProfileResponse updateTeacherProfile(Long profileId, TeacherProfileRequest request, Long actorId) {
        log.info("Updating teacher profile: {}", profileId);

        TeacherProfile profile = teacherProfileRepository.findById(profileId)
                .orElseThrow(() -> new NoSuchElementException("Teacher profile not found: " + profileId));

        profile.setBio(request.getBio());
        profile.setQualifications(request.getQualifications());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setPhone(request.getPhone());

        TeacherProfile updated = teacherProfileRepository.save(profile);

        // Remove old expertise and add new ones
        subjectExpertiseRepository.deleteByTeacherId(profileId);

        for (TeacherProfileRequest.SubjectExpertiseRequest expertiseRequest : request.getSubjectExpertise()) {
            Subject subject = subjectRepository.findById(expertiseRequest.getSubjectId())
                    .orElseThrow(() -> new NoSuchElementException("Subject not found: " + expertiseRequest.getSubjectId()));

            TeacherSubjectExpertise expertise = TeacherSubjectExpertise.builder()
                    .teacher(updated)
                    .subject(subject)
                    .proficiencyLevel(expertiseRequest.getProficiencyLevel())
                    .build();

            subjectExpertiseRepository.save(expertise);
        }

        auditLogService.logAction(
                actorId,
                "UPDATE",
                "TEACHER_PROFILE",
                profileId,
                null,
                null,
                "SUCCESS"
        );

        log.info("Teacher profile updated successfully: {} (ID: {})", updated.getUser().getUsername(), profileId);
        return toResponse(updated);
    }

    public void deleteTeacherProfile(Long profileId, Long actorId) {
        log.info("Deleting teacher profile: {}", profileId);

        TeacherProfile profile = teacherProfileRepository.findById(profileId)
                .orElseThrow(() -> new NoSuchElementException("Teacher profile not found: " + profileId));

        subjectExpertiseRepository.deleteByTeacherId(profileId);
        teacherProfileRepository.delete(profile);

        auditLogService.logAction(
                actorId,
                "DELETE",
                "TEACHER_PROFILE",
                profileId,
                null,
                null,
                "SUCCESS"
        );

        log.info("Teacher profile deleted successfully: {} (ID: {})", profile.getUser().getUsername(), profileId);
    }

    @Transactional(readOnly = true)
    public TeacherProfileResponse getTeacherProfileById(Long profileId) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
                .orElseThrow(() -> new NoSuchElementException("Teacher profile not found: " + profileId));
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public TeacherProfileResponse getTeacherProfileByUserId(Long userId) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Teacher profile not found for user: " + userId));
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public Page<TeacherProfileResponse> getAllTeacherProfiles(Pageable pageable) {
        return teacherProfileRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TeacherProfileResponse> getTeachersBySubject(Long subjectId) {
        return subjectExpertiseRepository.findBySubjectId(subjectId).stream()
                .map(expertise -> toResponse(expertise.getTeacher()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TeacherProfileResponse> getTeachersByProficiencyLevel(String proficiencyLevel) {
        return subjectExpertiseRepository.findByProficiencyLevel(proficiencyLevel).stream()
                .map(expertise -> toResponse(expertise.getTeacher()))
                .distinct()
                .collect(Collectors.toList());
    }

    private TeacherProfileResponse toResponse(TeacherProfile profile) {
        List<TeacherProfileResponse.SubjectExpertiseResponse> expertise = subjectExpertiseRepository
                .findByTeacherId(profile.getId()).stream()
                .map(exp -> TeacherProfileResponse.SubjectExpertiseResponse.builder()
                        .subjectId(exp.getSubject().getId())
                        .subjectName(exp.getSubject().getNameEn())
                        .proficiencyLevel(exp.getProficiencyLevel())
                        .build())
                .collect(Collectors.toList());

        return TeacherProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .username(profile.getUser().getUsername())
                .email(profile.getUser().getEmail())
                .bio(profile.getBio())
                .qualifications(profile.getQualifications())
                .experienceYears(profile.getExperienceYears())
                .phone(profile.getPhone())
                .subjectExpertise(expertise)
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
