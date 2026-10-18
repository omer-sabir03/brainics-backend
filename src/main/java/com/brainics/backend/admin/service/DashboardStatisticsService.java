package com.brainics.backend.admin.service;

import com.brainics.backend.admin.dto.DashboardStats;
import com.brainics.backend.admin.repository.AuditLogRepository;
import com.brainics.backend.teacher.repository.TeacherProfileRepository;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.repository.AdmissionEnquiryRepository;
import com.brainics.backend.enquiry.repository.EnquiryStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardStatisticsService {

    private final AdmissionEnquiryRepository enquiryRepository;
    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AuditLogRepository auditLogRepository;
    private final EnquiryStatusHistoryRepository enquiryStatusHistoryRepository;

    public DashboardStats getDashboardStats() {
        log.info("Calculating dashboard statistics");

        return DashboardStats.builder()
                .enquiries(getEnquiryStats())
                .users(getUserStats())
                .teachers(getTeacherStats())
                .academic(getAcademicStats())
                .build();
    }

    private DashboardStats.EnquiryStats getEnquiryStats() {
        List<AdmissionEnquiry> allEnquiries = enquiryRepository.findAll();

        long totalEnquiries = allEnquiries.size();
        long newEnquiries = allEnquiries.stream()
                .filter(e -> "NEW".equals(e.getStatus()))
                .count();
        long contactedEnquiries = allEnquiries.stream()
                .filter(e -> "CONTACTED".equals(e.getStatus()))
                .count();
        long enrolledEnquiries = allEnquiries.stream()
                .filter(e -> "ENROLLED".equals(e.getStatus()))
                .count();

        Map<String, Long> statusBreakdown = allEnquiries.stream()
                .collect(Collectors.groupingByConcurrent(
                        AdmissionEnquiry::getStatus,
                        Collectors.counting()
                ));

        double conversionRate = totalEnquiries > 0 ? (double) enrolledEnquiries / totalEnquiries * 100 : 0.0;

        long avgResponseTimeHours = calculateAvgResponseTime();

        return DashboardStats.EnquiryStats.builder()
                .totalEnquiries(totalEnquiries)
                .newEnquiries(newEnquiries)
                .contactedEnquiries(contactedEnquiries)
                .enrolledEnquiries(enrolledEnquiries)
                .statusBreakdown(statusBreakdown)
                .conversionRate(conversionRate)
                .avgResponseTimeHours(avgResponseTimeHours)
                .build();
    }

    private DashboardStats.UserStats getUserStats() {
        List<AppUser> allUsers = userRepository.findAll();

        long totalUsers = allUsers.size();
        long adminCount = countUsersByRole(allUsers, "ADMIN");
        long teacherCount = countUsersByRole(allUsers, "TEACHER");
        long studentCount = countUsersByRole(allUsers, "STUDENT");
        long activeUsers = allUsers.stream().filter(AppUser::isEnabled).count();
        long inactiveUsers = allUsers.stream().filter(u -> !u.isEnabled()).count();

        return DashboardStats.UserStats.builder()
                .totalUsers(totalUsers)
                .adminCount(adminCount)
                .teacherCount(teacherCount)
                .studentCount(studentCount)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .build();
    }

    private DashboardStats.TeacherStats getTeacherStats() {
        long totalTeachers = teacherProfileRepository.count();
        long totalClassesOffered = 0;  // Will need enrollment table to calculate
        double avgClassesPerTeacher = totalTeachers > 0 ? totalClassesOffered / (double) totalTeachers : 0.0;
        double avgStudentsPerTeacher = 0.0;  // Will need enrollment table

        return DashboardStats.TeacherStats.builder()
                .totalTeachers(totalTeachers)
                .avgClassesPerTeacher(avgClassesPerTeacher)
                .avgStudentsPerTeacher(avgStudentsPerTeacher)
                .totalClassesOffered(totalClassesOffered)
                .build();
    }

    private DashboardStats.AcademicStats getAcademicStats() {
        long totalStudentsEnrolled = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "STUDENT".equals(r.getName())))
                .count();

        double avgAttendanceRate = 0.0;  // Will need attendance table
        long classesActive = 0;  // Will need to count active classes

        return DashboardStats.AcademicStats.builder()
                .totalStudentsEnrolled(totalStudentsEnrolled)
                .avgAttendanceRate(avgAttendanceRate)
                .classesActive(classesActive)
                .build();
    }

    private long countUsersByRole(List<AppUser> users, String roleName) {
        return users.stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> roleName.equals(r.getName())))
                .count();
    }

    private long calculateAvgResponseTime() {
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

    public DashboardStats.EnquiryStats getEnquiryStatsByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        List<AdmissionEnquiry> enquiries = enquiryRepository.findAll().stream()
                .filter(e -> e.getCreatedAt().isAfter(startDate) && e.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());

        long totalEnquiries = enquiries.size();
        long newEnquiries = enquiries.stream().filter(e -> "NEW".equals(e.getStatus())).count();
        long contactedEnquiries = enquiries.stream().filter(e -> "CONTACTED".equals(e.getStatus())).count();
        long enrolledEnquiries = enquiries.stream().filter(e -> "ENROLLED".equals(e.getStatus())).count();

        Map<String, Long> statusBreakdown = enquiries.stream()
                .collect(Collectors.groupingByConcurrent(
                        AdmissionEnquiry::getStatus,
                        Collectors.counting()
                ));

        double conversionRate = totalEnquiries > 0 ? (double) enrolledEnquiries / totalEnquiries * 100 : 0.0;

        return DashboardStats.EnquiryStats.builder()
                .totalEnquiries(totalEnquiries)
                .newEnquiries(newEnquiries)
                .contactedEnquiries(contactedEnquiries)
                .enrolledEnquiries(enrolledEnquiries)
                .statusBreakdown(statusBreakdown)
                .conversionRate(conversionRate)
                .avgResponseTimeHours(0L)
                .build();
    }

    public Map<String, Long> getActionCounts() {
        List<String> allActions = auditLogRepository.findAll().stream()
                .map(log -> log.getAction())
                .collect(Collectors.toList());

        return allActions.stream()
                .collect(Collectors.groupingByConcurrent(
                        String::toString,
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getResourceTypeCounts() {
        List<String> allResourceTypes = auditLogRepository.findAll().stream()
                .map(log -> log.getResourceType())
                .collect(Collectors.toList());

        return allResourceTypes.stream()
                .collect(Collectors.groupingByConcurrent(
                        String::toString,
                        Collectors.counting()
                ));
    }
}
