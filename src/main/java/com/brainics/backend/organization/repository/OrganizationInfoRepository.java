package com.brainics.backend.organization.repository;

import com.brainics.backend.organization.model.OrganizationInfo;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationInfoRepository extends CrudRepository<OrganizationInfo, Long> {
}
