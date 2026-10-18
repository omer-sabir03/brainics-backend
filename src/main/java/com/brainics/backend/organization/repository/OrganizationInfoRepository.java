package com.brainics.backend.organization.repository;

import com.brainics.backend.organization.model.OrganizationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationInfoRepository extends JpaRepository<OrganizationInfo, Long> {
}
