package com.brainics.backend.portal;

import com.brainics.backend.admin.dto.TeacherProfileResponse;
import com.brainics.backend.admin.dto.TeacherSelfUpdateRequest;
import com.brainics.backend.admin.service.TeacherService;
import com.brainics.backend.enquiry.dto.EnquiryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Teacher portal: each endpoint acts only on the signed-in teacher's own data
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherPortalController {

    private final TeacherService teacherService;
    private final PortalService portalService;

    @GetMapping("/profile")
    public ResponseEntity<TeacherProfileResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(teacherService.getOwnProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<TeacherProfileResponse> updateProfile(
            @Valid @RequestBody TeacherSelfUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(teacherService.updateOwnProfile(authentication.getName(), request));
    }

    @GetMapping("/enquiries")
    public ResponseEntity<List<EnquiryResponse>> getAssignedEnquiries(Authentication authentication) {
        return ResponseEntity.ok(portalService.assignedEnquiries(authentication.getName()));
    }
}
