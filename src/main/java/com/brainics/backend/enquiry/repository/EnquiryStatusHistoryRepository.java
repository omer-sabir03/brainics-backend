package com.brainics.backend.enquiry.repository;

import com.brainics.backend.enquiry.model.EnquiryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnquiryStatusHistoryRepository extends JpaRepository<EnquiryStatusHistory, Long> {
    List<EnquiryStatusHistory> findByEnquiryIdOrderByCreatedAtDesc(Long enquiryId);
}
