# Phase 2 Implementation Plan - Admin Dashboard Backend

## 📋 Overview

**Phase 2** adds comprehensive admin and teacher dashboard functionality to the Brainics backend.

**Timeline:** 4-6 weeks | **Sprints:** 4 | **Duration:** 6-8 weeks after Phase 1

---

## 🎯 Phase 2 Objectives

### Primary Goals
1. ✅ Admin user management (create, read, update, delete users)
2. ✅ Admin dashboard with statistics
3. ✅ Enquiry management system
4. ✅ Teacher profile management
5. ✅ Teacher dashboard APIs
6. ✅ Role-based access control (ADMIN, TEACHER, STUDENT)
7. ✅ Activity logging & audit trails

### Key Features
- Admin panel for user/enquiry management
- Real-time dashboard statistics
- Teacher profile management
- Class & course management
- Admin activity audit trail
- Bulk operations support
- Report generation

---

## 📊 Database Schema (Phase 2)

### New Migrations: V9-V12

#### V9: Teacher Profiles
```sql
teacher_profiles (
  id, user_id, bio, qualifications, 
  experience_years, subject_expertise[],
  phone, created_at
)
```

#### V10: Admin Audit Trail
```sql
admin_audit_logs (
  id, user_id, action, resource_type,
  resource_id, old_values, new_values,
  ip_address, created_at
)
```

#### V11: Enquiry Status History
```sql
enquiry_status_history (
  id, enquiry_id, old_status, new_status,
  changed_by_id, reason, created_at
)
```

#### V12: Admin Dashboard Preferences
```sql
admin_preferences (
  id, user_id, dashboard_layout,
  preferred_metrics, notification_settings,
  created_at
)
```

---

## 🏗️ New Modules

### 1. admin/ - Admin Management
```
admin/
├── model/
│   ├── AdminUser.java (extends AppUser)
│   ├── AuditLog.java
│   └── EnquiryStatusHistory.java
├── repository/
│   ├── AdminUserRepository.java
│   ├── AuditLogRepository.java
│   └── StatusHistoryRepository.java
├── service/
│   ├── AdminUserService.java
│   ├── AuditLogService.java
│   └── DashboardStatisticsService.java
├── dto/
│   ├── AdminUserRequest.java
│   ├── AdminUserResponse.java
│   ├── DashboardStats.java
│   └── AuditLogResponse.java
└── controller/
    ├── AdminUserController.java
    └── DashboardController.java
```

### 2. teacher/ - Teacher Dashboard
```
teacher/
├── model/
│   ├── TeacherProfile.java
│   └── TeacherClass.java
├── repository/
│   ├── TeacherProfileRepository.java
│   └── TeacherClassRepository.java
├── service/
│   ├── TeacherService.java
│   ├── ClassManagementService.java
│   └── StudentManagementService.java
├── dto/
│   ├── TeacherProfileRequest.java
│   ├── TeacherProfileResponse.java
│   ├── TeacherDashboardStats.java
│   └── ClassResponse.java
└── controller/
    ├── TeacherProfileController.java
    ├── TeacherClassController.java
    └── TeacherDashboardController.java
```

### 3. Enhanced enquiry/ Module
```
enquiry/
├── service/
│   ├── EnquiryStatusService.java (NEW)
│   ├── EnquiryReportService.java (NEW)
│   └── EnquiryAnalyticsService.java (NEW)
└── controller/
    └── AdminEnquiryController.java (ENHANCED)
```

---

## 🔌 API Endpoints (Phase 2)

### Admin User Management
```
POST    /api/admin/users                    # Create new user
GET     /api/admin/users                    # List all users (paginated)
GET     /api/admin/users/{id}               # Get user details
PATCH   /api/admin/users/{id}               # Update user
DELETE  /api/admin/users/{id}               # Deactivate user
PATCH   /api/admin/users/{id}/role          # Change user role
PATCH   /api/admin/users/{id}/status        # Enable/disable user
```

### Admin Dashboard
```
GET     /api/admin/dashboard/stats          # Overall statistics
GET     /api/admin/dashboard/enquiries      # Enquiry insights
GET     /api/admin/dashboard/users          # User statistics
GET     /api/admin/dashboard/activity       # Recent activity
GET     /api/admin/dashboard/reports        # Report data
```

### Enquiry Management (Enhanced)
```
GET     /api/admin/enquiries                # List enquiries (paginated)
GET     /api/admin/enquiries/{id}           # Get enquiry details
PATCH   /api/admin/enquiries/{id}/status    # Update status
PATCH   /api/admin/enquiries/{id}/assign    # Assign to staff
POST    /api/admin/enquiries/{id}/notes     # Add notes
GET     /api/admin/enquiries/{id}/history   # Status history
DELETE  /api/admin/enquiries/{id}           # Delete enquiry
```

### Teacher Profile Management
```
POST    /api/admin/teachers                 # Create teacher
GET     /api/admin/teachers                 # List teachers (paginated)
GET     /api/admin/teachers/{id}            # Get teacher profile
PATCH   /api/admin/teachers/{id}            # Update profile
DELETE  /api/admin/teachers/{id}            # Remove teacher
POST    /api/admin/teachers/{id}/classes    # Assign classes
GET     /api/admin/teachers/{id}/students   # View assigned students
```

### Teacher Dashboard
```
GET     /api/teacher/dashboard/stats        # Teacher statistics
GET     /api/teacher/profile                # Own profile
PATCH   /api/teacher/profile                # Update own profile
GET     /api/teacher/classes                # My classes
GET     /api/teacher/students               # My students
GET     /api/teacher/activity               # Activity log
```

### Audit & Logging
```
GET     /api/admin/audit/logs               # Audit trail (paginated)
GET     /api/admin/audit/logs/{userId}      # User activity
POST    /api/admin/audit/export             # Export audit data
```

---

## 💼 Admin User Management Features

### User CRUD Operations
- Create users (all roles: ADMIN, TEACHER, STUDENT)
- List users with filtering (by role, status, created date)
- Update user details (name, email, phone)
- Deactivate/delete users (soft delete)
- Change user roles
- Enable/disable accounts
- Bulk import users (CSV)
- Bulk actions

### User Validations
- Email uniqueness check
- Phone format validation
- Required fields validation
- Role permissions check
- Duplicate prevention

### User Management Workflows
1. Create new user → Send invitation email
2. Update user → Log change + notify
3. Delete user → Soft delete + audit log
4. Change role → Update permissions
5. Disable account → Revoke access

---

## 📈 Dashboard Statistics

### Enquiry Metrics
- Total enquiries (all time)
- New enquiries (today, this week, this month)
- Status breakdown (NEW, REVIEWED, CONTACTED, ENROLLED, CLOSED)
- Conversion rate
- Average response time
- Source breakdown (WEB, WHATSAPP, PHONE, REFERRAL)

### User Metrics
- Total users by role
- Active vs. inactive
- Registration trend
- User growth chart
- Last login stats

### Teacher Metrics
- Total teachers
- Classes per teacher
- Student count per teacher
- Assignment completion rate
- Student feedback rating

### Academic Metrics
- Classes offered
- Total students enrolled
- Attendance rate
- Performance metrics
- Subject popularity

---

## 🔐 Security & Authorization

### Role-Based Access Control
```
ADMIN:
  - Manage all users (create, read, update, delete)
  - View all enquiries & manage status
  - View dashboard stats
  - Access audit logs
  - Manage teachers
  - Generate reports

TEACHER:
  - View own profile
  - View own classes
  - Manage students in classes
  - View personal stats
  - Submit marks/attendance
  - Cannot manage other teachers

STUDENT:
  - View own profile
  - View enrolled courses
  - Cannot access admin features
```

### Audit & Logging
- Log all admin actions (create, update, delete)
- Track enquiry status changes with reason
- Record IP address for security
- Maintain change history
- User activity tracking
- Export audit logs

---

## 📋 Implementation Sprints

### Sprint 1: Database & Models (Weeks 1-2)
- Create V9-V12 migrations
- Implement entity classes
- Add repositories
- Add indexes for performance

### Sprint 2: Services & Business Logic (Weeks 2-3)
- Admin user service (CRUD, role management)
- Audit logging service
- Dashboard statistics service
- Teacher management service
- Enquiry management enhancements

### Sprint 3: Controllers & APIs (Weeks 3-4)
- Admin user controller
- Admin dashboard controller
- Enhanced enquiry controller
- Teacher controller
- Admin audit log controller
- Rate limiting & validation

### Sprint 4: Advanced Features & Testing (Weeks 4-5)
- Bulk operations
- Report generation
- Email notifications (user creation, role change)
- Comprehensive testing
- Load testing
- Security verification

---

## 🗂️ File Structure

```
src/main/java/com/brainics/backend/
├── admin/                           (NEW - 12 files)
│   ├── model/
│   ├── repository/
│   ├── service/
│   ├── dto/
│   └── controller/
├── teacher/                         (NEW - 12 files)
│   ├── model/
│   ├── repository/
│   ├── service/
│   ├── dto/
│   └── controller/
├── enquiry/                         (ENHANCED - +4 files)
│   └── (new services/controllers)
└── (other existing modules)

src/main/resources/
├── db/migration/
│   ├── V9__create_teacher_profiles.sql
│   ├── V10__create_admin_audit_logs.sql
│   ├── V11__create_enquiry_status_history.sql
│   └── V12__create_admin_preferences.sql
└── (other existing resources)

src/test/java/
├── admin/                           (NEW - 4 test classes)
├── teacher/                         (NEW - 3 test classes)
└── enquiry/                         (ENHANCED - +2 test classes)
```

---

## 📊 Database Diagram (Phase 2 Additions)

```
users (existing)
  ├── teacher_profiles (NEW)
  │   ├── (user_id FK)
  │   └── (subject_id[] FK)
  │
  ├── admin_audit_logs (NEW)
  │   └── (user_id FK to actor)
  │
  └── admin_preferences (NEW)
      └── (user_id FK)

admission_enquiries (existing)
  └── enquiry_status_history (NEW)
      ├── (enquiry_id FK)
      └── (changed_by_id FK to users)
```

---

## 🧪 Testing Strategy

### Unit Tests
- Admin user service logic
- Statistics calculations
- Permission checks
- Audit logging
- Validations

### Integration Tests
- Full admin workflows
- User creation & deletion
- Role changes
- Dashboard data accuracy
- Teacher profile management

### End-to-End Tests
- Complete admin user flow
- Admin dashboard view
- Enquiry management workflow
- Teacher dashboard access

---

## 📦 Dependencies (Same as Phase 1)
No new dependencies required. Uses existing:
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- Testcontainers
- Lombok
- Jackson

---

## 🚀 Deployment Checklist

### Pre-Deployment
- All tests passing
- Code review completed
- Security review passed
- Database migrations tested
- Audit logging verified

### Deployment
- Run migrations V9-V12
- Verify admin user can login
- Test all CRUD operations
- Verify audit logging works
- Test dashboard stats

### Post-Deployment
- Monitor admin API performance
- Check audit logs for errors
- Verify user creation emails sent
- Monitor dashboard load times

---

## 🔮 Phase 3 Foundations (Already Prepared)

Infrastructure ready for Phase 3:
- Course/class enrollment system (TeacherClass entity)
- Assignment system foundations
- Results tracking foundation
- Attendance system foundation
- Notifications infrastructure

---

## ✨ Success Criteria

Phase 2 will be considered complete when:
- ✅ All 4 sprints completed
- ✅ 28+ new API endpoints working
- ✅ Admin dashboard fully functional
- ✅ Teacher dashboard accessible
- ✅ Audit logging operational
- ✅ All tests passing (30+ test methods)
- ✅ Security verified
- ✅ Documentation complete
- ✅ Code pushed to GitHub

---

## 📞 References

- Phase 1: PHASE1_SUMMARY.md
- Testing: TESTING_GUIDE.md
- Build: BUILD_INSTRUCTIONS.md
- Security: Check SecurityConfig.java
