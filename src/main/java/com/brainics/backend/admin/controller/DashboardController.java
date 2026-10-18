package com.brainics.backend.admin.controller;

import com.brainics.backend.admin.dto.DashboardStats;
import com.brainics.backend.admin.service.DashboardStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardStatisticsService dashboardStatisticsService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        log.info("Fetching complete dashboard statistics");
        DashboardStats stats = dashboardStatisticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/enquiry-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats.EnquiryStats> getEnquiryStats() {
        log.info("Fetching enquiry statistics");
        DashboardStats stats = dashboardStatisticsService.getDashboardStats();
        return ResponseEntity.ok(stats.getEnquiries());
    }

    @GetMapping("/enquiry-stats/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats.EnquiryStats> getEnquiryStatsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        log.info("Fetching enquiry statistics for date range: {} to {}", startDate, endDate);
        DashboardStats.EnquiryStats stats = dashboardStatisticsService.getEnquiryStatsByDateRange(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats.UserStats> getUserStats() {
        log.info("Fetching user statistics");
        DashboardStats stats = dashboardStatisticsService.getDashboardStats();
        return ResponseEntity.ok(stats.getUsers());
    }

    @GetMapping("/teacher-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats.TeacherStats> getTeacherStats() {
        log.info("Fetching teacher statistics");
        DashboardStats stats = dashboardStatisticsService.getDashboardStats();
        return ResponseEntity.ok(stats.getTeachers());
    }

    @GetMapping("/academic-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats.AcademicStats> getAcademicStats() {
        log.info("Fetching academic statistics");
        DashboardStats stats = dashboardStatisticsService.getDashboardStats();
        return ResponseEntity.ok(stats.getAcademic());
    }

    @GetMapping("/action-counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getActionCounts() {
        log.info("Fetching action counts");
        Map<String, Long> counts = dashboardStatisticsService.getActionCounts();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/resource-type-counts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getResourceTypeCounts() {
        log.info("Fetching resource type counts");
        Map<String, Long> counts = dashboardStatisticsService.getResourceTypeCounts();
        return ResponseEntity.ok(counts);
    }
}
