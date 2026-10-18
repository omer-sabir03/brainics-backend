package com.brainics.backend.enquiry.service;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.academy.model.Subject;
import com.brainics.backend.academy.repository.ClassRepository;
import com.brainics.backend.academy.repository.SubjectRepository;
import com.brainics.backend.common.exception.BadRequestException;
import com.brainics.backend.enquiry.dto.CreateEnquiryRequest;
import com.brainics.backend.enquiry.dto.EnquiryResponse;
import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.repository.AdmissionEnquiryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class EnquiryServiceIntegrationTest {

    @Autowired
    private EnquiryService enquiryService;

    @Autowired
    private AdmissionEnquiryRepository enquiryRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private Class testClass;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        // Create test class
        testClass = Class.builder()
            .classNumber(5)
            .description("Class 5")
            .isActive(true)
            .build();
        testClass = classRepository.save(testClass);

        // Create test subject
        testSubject = Subject.builder()
            .nameEn("Mathematics")
            .nameUrdu("ریاضی")
            .description("Basic Mathematics")
            .isActive(true)
            .build();
        testSubject = subjectRepository.save(testSubject);
    }

    @Test
    void testSubmitValidEnquiry() {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Ahmed Ali")
            .parentEmail("ahmed@example.com")
            .parentPhone("+923001234567")
            .studentName("Ali Ahmed")
            .classId(testClass.getId())
            .preferredSubjectIds(Arrays.asList(testSubject.getId()))
            .message("Interested in mathematics classes")
            .build();

        EnquiryResponse response = enquiryService.submitEnquiry(request, "192.168.1.1");

        assertNotNull(response.getId());
        assertEquals("Ahmed Ali", response.getParentName());
        assertEquals("ahmed@example.com", response.getParentEmail());
        assertEquals("NEW", response.getStatus());
        assertEquals(1, response.getPreferredSubjects().size());
    }

    @Test
    void testSubmitEnquiryWithLocalPhoneFormat() {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Fatima Khan")
            .parentEmail("fatima@example.com")
            .parentPhone("03001234567")
            .studentName("Sara Khan")
            .classId(testClass.getId())
            .build();

        EnquiryResponse response = enquiryService.submitEnquiry(request, "192.168.1.1");

        assertNotNull(response.getId());
        assertEquals("+923001234567", response.getParentPhone());
    }

    @Test
    void testDuplicateEnquiryPrevention() {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Test Parent")
            .parentEmail("duplicate@example.com")
            .parentPhone("+923001234567")
            .studentName("Test Student")
            .classId(testClass.getId())
            .build();

        // First submission should succeed
        EnquiryResponse firstResponse = enquiryService.submitEnquiry(request, "192.168.1.1");
        assertNotNull(firstResponse.getId());

        // Second submission should fail
        assertThrows(BadRequestException.class, () -> {
            enquiryService.submitEnquiry(request, "192.168.1.1");
        });
    }

    @Test
    void testEnquiryValidationFailures() {
        CreateEnquiryRequest invalidRequest = CreateEnquiryRequest.builder()
            .parentName("")
            .parentEmail("invalid-email")
            .parentPhone("12345")
            .studentName("Test")
            .classId(testClass.getId())
            .build();

        assertThrows(BadRequestException.class, () -> {
            enquiryService.submitEnquiry(invalidRequest, "192.168.1.1");
        });
    }

    @Test
    void testGetEnquiryById() {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("John Doe")
            .parentEmail("john@example.com")
            .parentPhone("+923001234567")
            .studentName("Jane Doe")
            .classId(testClass.getId())
            .build();

        EnquiryResponse submitted = enquiryService.submitEnquiry(request, "192.168.1.1");
        EnquiryResponse retrieved = enquiryService.getEnquiryById(submitted.getId());

        assertEquals(submitted.getId(), retrieved.getId());
        assertEquals("John Doe", retrieved.getParentName());
    }
}
