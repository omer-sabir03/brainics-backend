package com.brainics.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    private EnquiryStats enquiries;
    private UserStats users;
    private TeacherStats teachers;
    private AcademicStats academic;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnquiryStats {
        private Long totalEnquiries;
        private Long newEnquiries;
        private Long contactedEnquiries;
        private Long enrolledEnquiries;
        private Map<String, Long> statusBreakdown;
        private Double conversionRate;
        private Long avgResponseTimeHours;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserStats {
        private Long totalUsers;
        private Long adminCount;
        private Long teacherCount;
        private Long studentCount;
        private Long activeUsers;
        private Long inactiveUsers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeacherStats {
        private Long totalTeachers;
        private Double avgClassesPerTeacher;
        private Double avgStudentsPerTeacher;
        private Long totalClassesOffered;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AcademicStats {
        private Long totalStudentsEnrolled;
        private Double avgAttendanceRate;
        private Long classesActive;
    }
}
