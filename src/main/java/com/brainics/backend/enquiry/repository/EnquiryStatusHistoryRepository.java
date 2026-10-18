package com.brainics.backend.enquiry.repository;

import com.brainics.backend.enquiry.model.EnquiryStatusHistory;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnquiryStatusHistoryRepository extends CrudRepository<EnquiryStatusHistory, Long> {
    List<EnquiryStatusHistory> findByEnquiryIdOrderByCreatedAtDesc(Long enquiryId);
}
