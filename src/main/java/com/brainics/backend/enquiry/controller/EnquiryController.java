package com.brainics.backend.enquiry.controller;

import com.brainics.backend.common.ApiResponse;
import com.brainics.backend.enquiry.dto.CreateEnquiryRequest;
import com.brainics.backend.enquiry.dto.EnquiryResponse;
import com.brainics.backend.enquiry.service.EnquiryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EnquiryController {

    private final EnquiryService enquiryService;

    @PostMapping("/api/public/enquiry/submit")
    public ResponseEntity<ApiResponse<EnquiryResponse>> submitEnquiry(
        @Valid @RequestBody CreateEnquiryRequest request,
        HttpServletRequest httpRequest) {

        String clientIp = getClientIpAddress(httpRequest);
        EnquiryResponse response = enquiryService.submitEnquiry(request, clientIp);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Enquiry submitted successfully"));
    }

    @GetMapping("/api/public/enquiry/status/{enquiryId}")
    public ResponseEntity<ApiResponse<EnquiryResponse>> getEnquiryStatus(@PathVariable Long enquiryId) {
        EnquiryResponse response = enquiryService.getEnquiryById(enquiryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/api/admin/enquiries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<EnquiryResponse>>> getAllEnquiries(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<EnquiryResponse> enquiries = enquiryService.getAllEnquiries(pageable);
        return ResponseEntity.ok(ApiResponse.success(enquiries));
    }

    @GetMapping("/api/admin/enquiries/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<EnquiryResponse>>> getEnquiriesByStatus(
        @PathVariable String status,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<EnquiryResponse> enquiries = enquiryService.getEnquiriesByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(enquiries));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
