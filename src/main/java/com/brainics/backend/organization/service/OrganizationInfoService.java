package com.brainics.backend.organization.service;

import com.brainics.backend.common.exception.ResourceNotFoundException;
import com.brainics.backend.organization.dto.OrganizationInfoResponse;
import com.brainics.backend.organization.model.OrganizationInfo;
import com.brainics.backend.organization.repository.OrganizationInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationInfoService {

    private final OrganizationInfoRepository organizationInfoRepository;

    @Cacheable(value = "organization_info", key = "'default'", unless = "#result == null")
    public OrganizationInfoResponse getOrganizationInfo() {
        OrganizationInfo info = organizationInfoRepository.findById(1L)
            .orElseThrow(() -> {
                log.error("Organization info not found");
                return new ResourceNotFoundException("Organization information not found");
            });

        return toOrganizationInfoResponse(info);
    }

    public OrganizationInfo getOrganizationInfoEntity() {
        return organizationInfoRepository.findById(1L)
            .orElseThrow(() -> new ResourceNotFoundException("Organization information not found"));
    }

    private OrganizationInfoResponse toOrganizationInfoResponse(OrganizationInfo info) {
        return OrganizationInfoResponse.builder()
            .id(info.getId())
            .name(info.getName())
            .phonePrimary(info.getPhonePrimary())
            .phoneSecondary(info.getPhoneSecondary())
            .email(info.getEmail())
            .address(info.getAddress())
            .city(info.getCity())
            .postalCode(info.getPostalCode())
            .country(info.getCountry())
            .websiteUrl(info.getWebsiteUrl())
            .socialMediaLinks(info.getSocialMediaLinks())
            .latitude(info.getLatitude())
            .longitude(info.getLongitude())
            .officeHours(info.getOfficeHours())
            .build();
    }
}
