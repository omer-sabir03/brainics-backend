package com.brainics.backend.portal;

import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.enquiry.dto.EnquiryResponse;
import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.repository.AdmissionEnquiryRepository;
import com.brainics.backend.enquiry.service.EnquiryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdmissionEnquiryRepository enquiryRepository;

    @Mock
    private EnquiryService enquiryService;

    @InjectMocks
    private PortalService portalService;

    private AppUser teacher;
    private AppUser student;

    @BeforeEach
    void setUp() {
        teacher = AppUser.builder().id(10L).username("teacher1").passwordHash("x").build();
        student = AppUser.builder().id(20L).username("student1").passwordHash("x")
                .email("student@example.com").build();
    }

    @Test
    void assignedEnquiriesAreLimitedToTheTeacher() {
        AdmissionEnquiry enquiry = new AdmissionEnquiry();
        EnquiryResponse response = EnquiryResponse.builder().id(1L).build();
        when(userRepository.findByUsername("teacher1")).thenReturn(Optional.of(teacher));
        when(enquiryRepository.findAllByAssignedToIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(enquiry));
        when(enquiryService.toEnquiryResponse(enquiry)).thenReturn(response);

        List<EnquiryResponse> result = portalService.assignedEnquiries("teacher1");

        assertEquals(1, result.size());
        verify(enquiryRepository).findAllByAssignedToIdOrderByCreatedAtDesc(10L);
    }

    @Test
    void studentSeesOnlyEnquiriesMatchingTheirEmail() {
        AdmissionEnquiry enquiry = new AdmissionEnquiry();
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(student));
        when(enquiryRepository.findAllByParentEmailIgnoreCaseOrderByCreatedAtDesc("student@example.com"))
                .thenReturn(List.of(enquiry));
        when(enquiryService.toEnquiryResponse(enquiry)).thenReturn(EnquiryResponse.builder().id(2L).build());

        List<EnquiryResponse> result = portalService.enquiriesForStudent("student1");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getId() == 2L);
    }

    @Test
    void studentWithoutEmailGetsNoEnquiriesWithoutQuerying() {
        AppUser noEmail = AppUser.builder().id(21L).username("student2").passwordHash("x").build();
        when(userRepository.findByUsername("student2")).thenReturn(Optional.of(noEmail));

        assertTrue(portalService.enquiriesForStudent("student2").isEmpty());
        verify(enquiryRepository, never()).findAllByParentEmailIgnoreCaseOrderByCreatedAtDesc(org.mockito.ArgumentMatchers.any());
    }
}
