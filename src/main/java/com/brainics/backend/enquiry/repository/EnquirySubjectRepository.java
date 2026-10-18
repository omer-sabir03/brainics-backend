package com.brainics.backend.enquiry.repository;

import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.model.EnquirySubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnquirySubjectRepository extends JpaRepository<EnquirySubject, Long> {
    List<EnquirySubject> findByEnquiry(AdmissionEnquiry enquiry);
    void deleteByEnquiry(AdmissionEnquiry enquiry);
}
