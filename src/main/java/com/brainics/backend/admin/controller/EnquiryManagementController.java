package com.brainics.backend.admin.controller;

import com.brainics.backend.admin.service.EnquiryStatusService;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.model.EnquiryStatusHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/enquiries")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class EnquiryManagementController {

    private final EnquiryStatusService enquiryStatusService;
    private final UserRepository userRepository;

    @PutMapping("/{enquiryId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateEnquiryStatus(
            @PathVariable Long enquiryId,
            @RequestParam String newStatus,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        log.info("Updating enquiry {} status to {}", enquiryId, newStatus);
        Long userId = (Long) authentication.getPrincipal();
        enquiryStatusService.updateEnquiryStatus(enquiryId, newStatus, reason, notes, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{enquiryId}/status-history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnquiryStatusHistory>> getStatusHistory(@PathVariable Long enquiryId) {
        log.info("Fetching status history for enquiry: {}", enquiryId);
        List<EnquiryStatusHistory> history = enquiryStatusService.getStatusHistory(enquiryId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/avg-response-time")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getAverageResponseTime() {
        log.info("Fetching average enquiry response time");
        Long avgTime = enquiryStatusService.getAverageResponseTime();
        return ResponseEntity.ok(avgTime);
    }

    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdmissionEnquiry>> getEnquiriesByStatus(@PathVariable String status) {
        log.info("Fetching enquiries by status: {}", status);
        List<AdmissionEnquiry> enquiries = enquiryStatusService.getEnquiriesByStatus(status);
        return ResponseEntity.ok(enquiries);
    }

    @GetMapping("/unresponded")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdmissionEnquiry>> getUnrespondenEnquiries() {
        log.info("Fetching unresponded enquiries");
        List<AdmissionEnquiry> enquiries = enquiryStatusService.getUnrespondenEnquiries();
        return ResponseEntity.ok(enquiries);
    }

    @GetMapping("/old-unresponded")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdmissionEnquiry>> getEnquiriesWithoutResponse(
            @RequestParam(defaultValue = "7") int daysOld) {
        log.info("Fetching enquiries without response for {} days", daysOld);
        List<AdmissionEnquiry> enquiries = enquiryStatusService.getEnquiriesWithoutResponse(daysOld);
        return ResponseEntity.ok(enquiries);
    }

    @GetMapping("/status/{status}/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countEnquiriesByStatus(@PathVariable String status) {
        log.info("Counting enquiries by status: {}", status);
        Long count = enquiryStatusService.countEnquiriesByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/conversion-rate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getConversionRate() {
        log.info("Fetching enquiry conversion rate");
        Double rate = enquiryStatusService.getConversionRate();
        return ResponseEntity.ok(rate);
    }

    @PutMapping("/{enquiryId}/assign/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignEnquiryToTeacher(
            @PathVariable Long enquiryId,
            @PathVariable Long teacherId,
            Authentication authentication) {
        log.info("Assigning enquiry {} to teacher {}", enquiryId, teacherId);
  //      Long actorId = (Long) authentication.getPrincipal();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();

        Optional<AppUser> byUsername = userRepository.findByUsername(username);
        Long actorId = byUsername.isEmpty()?byUsername.get().getId(): -1;
        enquiryStatusService.assignEnquiryToTeacher(enquiryId, teacherId, actorId);
        return ResponseEntity.ok().build();
    }
}
