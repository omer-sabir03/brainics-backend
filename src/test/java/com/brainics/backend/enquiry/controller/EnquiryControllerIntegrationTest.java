package com.brainics.backend.enquiry.controller;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.academy.repository.ClassRepository;
import com.brainics.backend.academy.model.Subject;
import com.brainics.backend.academy.repository.SubjectRepository;
import com.brainics.backend.enquiry.dto.CreateEnquiryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class EnquiryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private Class testClass;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testClass = Class.builder()
            .classNumber(6)
            .description("Class 6")
            .isActive(true)
            .build();
        testClass = classRepository.save(testClass);

        testSubject = Subject.builder()
            .nameEn("English")
            .nameUrdu("انگریزی")
            .isActive(true)
            .build();
        testSubject = subjectRepository.save(testSubject);
    }

    @Test
    void testSubmitEnquirySuccess() throws Exception {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Hassan Ali")
            .parentEmail("hassan@example.com")
            .parentPhone("+923001234567")
            .studentName("Ali Hassan")
            .classId(testClass.getId())
            .preferredSubjectIds(Arrays.asList(testSubject.getId()))
            .message("Interested in English classes")
            .build();

        mockMvc.perform(post("/api/public/enquiry/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.parentName").value("Hassan Ali"))
            .andExpect(jsonPath("$.data.status").value("NEW"))
            .andExpect(jsonPath("$.data.preferredSubjects", hasSize(1)));
    }

    @Test
    void testSubmitEnquiryWithInvalidEmail() throws Exception {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Test Parent")
            .parentEmail("invalid-email")
            .parentPhone("+923001234567")
            .studentName("Test Student")
            .classId(testClass.getId())
            .build();

        mockMvc.perform(post("/api/public/enquiry/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testSubmitEnquiryWithInvalidPhone() throws Exception {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Test Parent")
            .parentEmail("test@example.com")
            .parentPhone("12345")
            .studentName("Test Student")
            .classId(testClass.getId())
            .build();

        mockMvc.perform(post("/api/public/enquiry/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetEnquiryStatus() throws Exception {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Zainab Khan")
            .parentEmail("zainab@example.com")
            .parentPhone("+923001234567")
            .studentName("Fatima Khan")
            .classId(testClass.getId())
            .build();

        String response = mockMvc.perform(post("/api/public/enquiry/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Long enquiryId = objectMapper.readTree(response).get("data").get("id").asLong();

        mockMvc.perform(get("/api/public/enquiry/status/" + enquiryId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(enquiryId))
            .andExpect(jsonPath("$.data.parentName").value("Zainab Khan"));
    }

    @Test
    void testSubmitDuplicateEnquiry() throws Exception {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Duplicate Test")
            .parentEmail("duplicate@example.com")
            .parentPhone("+923001234567")
            .studentName("Test Student")
            .classId(testClass.getId())
            .build();

        // First submission
        mockMvc.perform(post("/api/public/enquiry/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // Second submission (should fail)
        mockMvc.perform(post("/api/public/enquiry/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testRateLimitingNotTriggeredBeforeLimit() throws Exception {
        CreateEnquiryRequest request = CreateEnquiryRequest.builder()
            .parentName("Rate Test")
            .parentEmail("rate-test-{0}@example.com")
            .parentPhone("+923001234567")
            .studentName("Test Student")
            .classId(testClass.getId())
            .build();

        // Submit 3 enquiries (below rate limit of 5)
        for (int i = 0; i < 3; i++) {
            request.setParentEmail(String.format("rate-test-%d@example.com", i));
            mockMvc.perform(post("/api/public/enquiry/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        }
    }
}
