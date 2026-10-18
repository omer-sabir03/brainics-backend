package com.brainics.backend.enquiry.repository;

import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionEnquiryRepository extends CrudRepository<AdmissionEnquiry, Long> {
    Optional<AdmissionEnquiry> findByParentEmailIgnoreCaseAndClassEntityIdAndCreatedAtAfter(
        String parentEmail, Long classId, ZonedDateTime createdAfter);

    List<AdmissionEnquiry> findByParentEmailIgnoreCase(String parentEmail);
    List<AdmissionEnquiry> findByStatus(String status);
    List<AdmissionEnquiry> findAllByOrderByCreatedAtDesc();

    Page<AdmissionEnquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<AdmissionEnquiry> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    Page<AdmissionEnquiry> findByParentEmailIgnoreCaseOrderByCreatedAtDesc(String parentEmail, Pageable pageable);
}
