package com.brainics.backend.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationInfoResponse {
    private Long id;
    private String name;
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String websiteUrl;
    private Map<String, String> socialMediaLinks;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Map<String, Map<String, String>> officeHours;
}
