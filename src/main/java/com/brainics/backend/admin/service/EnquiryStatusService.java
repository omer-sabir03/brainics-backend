package com.brainics.backend.admin.service;

import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.model.EnquiryStatusHistory;
import com.brainics.backend.enquiry.repository.AdmissionEnquiryRepository;
import com.brainics.backend.enquiry.repository.EnquiryStatusHistoryRepository;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnquiryStatusService {

    private final AdmissionEnquiryRepository enquiryRepository;
    private final EnquiryStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public void updateEnquiryStatus(Long enquiryId, String newStatus, String reason, String notes, Long userId) {
        log.info("Updating enquiry {} status to {}", enquiryId, newStatus);

        AdmissionEnquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NoSuchElementException("Enquiry not found: " + enquiryId));

        AppUser changedBy = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        String oldStatus = enquiry.getStatus();

        // Record status change in history
        EnquiryStatusHistory history = EnquiryStatusHistory.builder()
                .enquiry(enquiry)
                .changedBy(changedBy)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .notes(notes)
                .build();

        statusHistoryRepository.save(history);

        // Update enquiry status
        enquiry.setStatus(newStatus);

        // Record first response time if transitioning from NEW to any other status
        if ("NEW".equals(oldStatus) && enquiry.getFirstResponseTime() == null) {
            enquiry.setFirstResponseTime(ZonedDateTime.now());
        }

        enquiryRepository.save(enquiry);

        auditLogService.logAction(
                userId,
                "UPDATE",
                "ENQUIRY_STATUS",
                enquiryId,
                null,
                null,
                "SUCCESS"
        );

        log.info("Enquiry {} status updated from {} to {}", enquiryId, oldStatus, newStatus);
    }

    public List<EnquiryStatusHistory> getStatusHistory(Long enquiryId) {
        return statusHistoryRepository.findByEnquiryIdOrderByCreatedAtDesc(enquiryId);
    }

    public Long getAverageResponseTime() {
        List<AdmissionEnquiry> enquiries = enquiryRepository.findAll();

        if (enquiries.isEmpty()) {
            return 0L;
        }

        long totalHours = 0;
        int count = 0;

        for (AdmissionEnquiry enquiry : enquiries) {
            if (enquiry.getFirstResponseTime() != null && enquiry.getCreatedAt() != null) {
                long hours = java.time.temporal.ChronoUnit.HOURS.between(
                        enquiry.getCreatedAt(),
                        enquiry.getFirstResponseTime()
                );
                totalHours += hours;
                count++;
            }
        }

        return count > 0 ? totalHours / count : 0L;
    }

    public List<AdmissionEnquiry> getEnquiriesByStatus(String status) {
        return enquiryRepository.findAll().stream()
                .filter(e -> status.equals(e.getStatus()))
                .collect(Collectors.toList());
    }

    public List<AdmissionEnquiry> getUnrespondenEnquiries() {
        return enquiryRepository.findAll().stream()
                .filter(e -> "NEW".equals(e.getStatus()))
                .collect(Collectors.toList());
    }

    public List<AdmissionEnquiry> getEnquiriesWithoutResponse(int daysOld) {
        ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(daysOld);

        return enquiryRepository.findAll().stream()
                .filter(e -> e.getFirstResponseTime() == null && e.getCreatedAt().isBefore(cutoffDate))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countEnquiriesByStatus(String status) {
        return enquiryRepository.findAll().stream()
                .filter(e -> status.equals(e.getStatus()))
                .count();
    }

    @Transactional(readOnly = true)
    public Double getConversionRate() {
        List<AdmissionEnquiry> allEnquiries = enquiryRepository.findAll();

        if (allEnquiries.isEmpty()) {
            return 0.0;
        }

        long enrolledCount = allEnquiries.stream()
                .filter(e -> "ENROLLED".equals(e.getStatus()))
                .count();

        return (double) enrolledCount / allEnquiries.size() * 100;
    }

    public void assignEnquiryToTeacher(Long enquiryId, Long teacherId, Long actorId) {
        log.info("Assigning enquiry {} to teacher {}", enquiryId, teacherId);

        AdmissionEnquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NoSuchElementException("Enquiry not found: " + enquiryId));

        AppUser teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NoSuchElementException("Teacher not found: " + teacherId));

        enquiry.setAssignedTo(teacher);
        enquiryRepository.save(enquiry);

        auditLogService.logAction(
                actorId,
                "UPDATE",
                "ENQUIRY_ASSIGNMENT",
                enquiryId,
                null,
                null,
                "SUCCESS"
        );

        log.info("Enquiry {} assigned to teacher {}", enquiryId, teacher.getUsername());
    }
}
