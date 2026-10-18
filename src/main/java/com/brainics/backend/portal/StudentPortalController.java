package com.brainics.backend.portal;

import com.brainics.backend.enquiry.dto.EnquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Student portal: each endpoint acts only on the signed-in student's own data
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentPortalController {

    private final PortalService portalService;

    @GetMapping("/enquiries")
    public ResponseEntity<List<EnquiryResponse>> getEnquiries(Authentication authentication) {
        return ResponseEntity.ok(portalService.enquiriesForStudent(authentication.getName()));
    }
}
