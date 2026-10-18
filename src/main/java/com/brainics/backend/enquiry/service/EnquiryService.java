package com.brainics.backend.enquiry.service;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.academy.model.Subject;
import com.brainics.backend.academy.service.ClassService;
import com.brainics.backend.academy.service.SubjectService;
import com.brainics.backend.common.exception.BadRequestException;
import com.brainics.backend.common.validator.PhoneValidator;
import com.brainics.backend.enquiry.dto.CreateEnquiryRequest;
import com.brainics.backend.enquiry.dto.EnquiryResponse;
import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.model.EnquirySubject;
import com.brainics.backend.enquiry.repository.AdmissionEnquiryRepository;
import com.brainics.backend.enquiry.repository.EnquirySubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EnquiryService {

    private final AdmissionEnquiryRepository enquiryRepository;
    private final EnquirySubjectRepository enquirySubjectRepository;
    private final ClassService classService;
    private final SubjectService subjectService;
    private final NotificationService notificationService;

    @Value("${app.enquiry.duplicate-check-hours:24}")
    private int duplicateCheckHours;

    public EnquiryResponse submitEnquiry(CreateEnquiryRequest request, String clientIpAddress) {
        validateEnquiryRequest(request);

        Class classEntity = classService.getClassByIdOrThrow(request.getClassId());

        checkForDuplicateEnquiry(request.getParentEmail(), classEntity.getId());

        String normalizedPhone = PhoneValidator.normalizePakistanPhone(request.getParentPhone());

        AdmissionEnquiry enquiry = AdmissionEnquiry.builder()
            .parentName(request.getParentName())
            .parentEmail(request.getParentEmail().toLowerCase())
            .parentPhone(normalizedPhone)
            .studentName(request.getStudentName())
            .classEntity(classEntity)
            .message(request.getMessage())
            .status("NEW")
            .source("WEB")
            .createdIpAddress(clientIpAddress)
            .build();

        AdmissionEnquiry savedEnquiry = enquiryRepository.save(enquiry);

        if (request.getPreferredSubjectIds() != null && !request.getPreferredSubjectIds().isEmpty()) {
            for (Long subjectId : request.getPreferredSubjectIds()) {
                Subject subject = subjectService.getSubjectByIdOrThrow(subjectId);
                EnquirySubject enquirySubject = EnquirySubject.builder()
                    .enquiry(savedEnquiry)
                    .subject(subject)
                    .build();
                enquirySubjectRepository.save(enquirySubject);
            }
        }

        notificationService.sendEnquiryNotifications(savedEnquiry);

        log.info("Enquiry submitted: id={}, parentEmail={}, studentClass={}",
            savedEnquiry.getId(), savedEnquiry.getParentEmail(), classEntity.getClassNumber());

        return toEnquiryResponse(savedEnquiry);
    }

    public EnquiryResponse getEnquiryById(Long enquiryId) {
        AdmissionEnquiry enquiry = enquiryRepository.findById(enquiryId)
            .orElseThrow(() -> new com.brainics.backend.common.exception.ResourceNotFoundException(
                "Enquiry not found with id: " + enquiryId));
        return toEnquiryResponse(enquiry);
    }

    public Page<EnquiryResponse> getAllEnquiries(Pageable pageable) {
        return enquiryRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(this::toEnquiryResponse);
    }

    public Page<EnquiryResponse> getEnquiriesByStatus(String status, Pageable pageable) {
        return enquiryRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
            .map(this::toEnquiryResponse);
    }

    @Transactional(readOnly = true)
    private void validateEnquiryRequest(CreateEnquiryRequest request) {
        if (request.getParentName() == null || request.getParentName().isBlank()) {
            throw new BadRequestException("Parent name is required");
        }
        if (request.getParentName().length() > 100) {
            throw new BadRequestException("Parent name must not exceed 100 characters");
        }

        if (request.getParentEmail() == null || request.getParentEmail().isBlank()) {
            throw new BadRequestException("Parent email is required");
        }
        if (!request.getParentEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BadRequestException("Invalid email format");
        }

        if (!PhoneValidator.isValidPakistanPhone(request.getParentPhone())) {
            throw new BadRequestException("Invalid phone format. Use +92XXXXXXXXXX or 0XXXXXXXXXX");
        }

        if (request.getStudentName() == null || request.getStudentName().isBlank()) {
            throw new BadRequestException("Student name is required");
        }
        if (request.getStudentName().length() > 100) {
            throw new BadRequestException("Student name must not exceed 100 characters");
        }

        if (request.getMessage() != null && request.getMessage().length() > 2000) {
            throw new BadRequestException("Message must not exceed 2000 characters");
        }
    }

    private void checkForDuplicateEnquiry(String parentEmail, Long classId) {
        ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(duplicateCheckHours);

        var existingEnquiry = enquiryRepository.findByParentEmailIgnoreCaseAndClassEntityIdAndCreatedAtAfter(
            parentEmail.toLowerCase(), classId, oneDayAgo);

        if (existingEnquiry.isPresent()) {
            log.warn("Duplicate enquiry attempt: email={}, classId={}", parentEmail, classId);
            throw new BadRequestException(
                "An enquiry for this email and class has already been submitted in the last 24 hours");
        }
    }

    private EnquiryResponse toEnquiryResponse(AdmissionEnquiry enquiry) {
        List<EnquiryResponse.SubjectPreferenceResponse> subjects = enquirySubjectRepository
            .findByEnquiry(enquiry).stream()
            .map(es -> EnquiryResponse.SubjectPreferenceResponse.builder()
                .subjectId(es.getSubject().getId())
                .subjectNameEn(es.getSubject().getNameEn())
                .subjectNameUrdu(es.getSubject().getNameUrdu())
                .build())
            .collect(Collectors.toList());

        return EnquiryResponse.builder()
            .id(enquiry.getId())
            .parentName(enquiry.getParentName())
            .parentEmail(enquiry.getParentEmail())
            .parentPhone(enquiry.getParentPhone())
            .studentName(enquiry.getStudentName())
            .classId(enquiry.getClassEntity().getId())
            .className("Class " + enquiry.getClassEntity().getClassNumber())
            .preferredSubjects(subjects)
            .message(enquiry.getMessage())
            .status(enquiry.getStatus())
            .source(enquiry.getSource())
            .createdAt(enquiry.getCreatedAt())
            .updatedAt(enquiry.getUpdatedAt())
            .build();
    }
}
