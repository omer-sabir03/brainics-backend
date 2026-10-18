package com.brainics.backend.organization.controller;

import com.brainics.backend.common.ApiResponse;
import com.brainics.backend.organization.dto.OrganizationInfoResponse;
import com.brainics.backend.organization.service.OrganizationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationInfoService organizationInfoService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<OrganizationInfoResponse>> getOrganizationInfo() {
        OrganizationInfoResponse info = organizationInfoService.getOrganizationInfo();
        return ResponseEntity.ok(ApiResponse.success(info));
    }
}
