package com.brainics.backend.portal;

import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.enquiry.dto.EnquiryResponse;
import com.brainics.backend.enquiry.repository.AdmissionEnquiryRepository;
import com.brainics.backend.enquiry.service.EnquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalService {

    private final UserRepository userRepository;
    private final AdmissionEnquiryRepository enquiryRepository;
    private final EnquiryService enquiryService;

    // Enquiries an admin has assigned to this teacher
    public List<EnquiryResponse> assignedEnquiries(String username) {
        AppUser teacher = findUser(username);
        return enquiryRepository.findAllByAssignedToIdOrderByCreatedAtDesc(teacher.getId()).stream()
                .map(enquiryService::toEnquiryResponse)
                .toList();
    }

    // Enquiries submitted with the student's account email (the link between an enquiry and a student account)
    public List<EnquiryResponse> enquiriesForStudent(String username) {
        AppUser student = findUser(username);
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            return List.of();
        }
        return enquiryRepository.findAllByParentEmailIgnoreCaseOrderByCreatedAtDesc(student.getEmail()).stream()
                .map(enquiryService::toEnquiryResponse)
                .toList();
    }

    private AppUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
    }
}
