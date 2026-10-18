package com.brainics.backend.academy.controller;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.academy.model.ClassSubject;
import com.brainics.backend.academy.model.Subject;
import com.brainics.backend.academy.repository.ClassRepository;
import com.brainics.backend.academy.repository.ClassSubjectRepository;
import com.brainics.backend.academy.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class AcademyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassSubjectRepository classSubjectRepository;

    @BeforeEach
    void setUp() {
        // Create test classes
        Class class1 = Class.builder()
            .classNumber(1)
            .description("Class 1")
            .isActive(true)
            .build();
        class1 = classRepository.save(class1);

        Class class2 = Class.builder()
            .classNumber(2)
            .description("Class 2")
            .isActive(true)
            .build();
        class2 = classRepository.save(class2);

        // Create test subjects
        Subject math = Subject.builder()
            .nameEn("Mathematics")
            .nameUrdu("ریاضی")
            .description("Math subject")
            .isActive(true)
            .build();
        math = subjectRepository.save(math);

        Subject science = Subject.builder()
            .nameEn("Science")
            .nameUrdu("سائنس")
            .description("Science subject")
            .isActive(true)
            .build();
        science = subjectRepository.save(science);

        // Link subjects to classes
        ClassSubject cs1 = ClassSubject.builder()
            .classEntity(class1)
            .subject(math)
            .isAvailable(true)
            .build();
        classSubjectRepository.save(cs1);

        ClassSubject cs2 = ClassSubject.builder()
            .classEntity(class1)
            .subject(science)
            .isAvailable(true)
            .build();
        classSubjectRepository.save(cs2);

        ClassSubject cs3 = ClassSubject.builder()
            .classEntity(class2)
            .subject(math)
            .isAvailable(true)
            .build();
        classSubjectRepository.save(cs3);
    }

    @Test
    void testGetAllClasses() throws Exception {
        mockMvc.perform(get("/api/public/academy/classes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))))
            .andExpect(jsonPath("$.data[0].classNumber").isNumber())
            .andExpect(jsonPath("$.data[0].subjects").isArray());
    }

    @Test
    void testGetClassById() throws Exception {
        Class testClass = classRepository.findByClassNumber(1).orElseThrow();

        mockMvc.perform(get("/api/public/academy/classes/" + testClass.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.classNumber").value(1))
            .andExpect(jsonPath("$.data.subjects", hasSize(2)));
    }

    @Test
    void testGetAllSubjects() throws Exception {
        mockMvc.perform(get("/api/public/academy/subjects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))))
            .andExpect(jsonPath("$.data[0].nameEn").isString())
            .andExpect(jsonPath("$.data[0].nameUrdu").isString());
    }

    @Test
    void testGetSubjectById() throws Exception {
        Subject testSubject = subjectRepository.findByNameEnIgnoreCase("Mathematics").orElseThrow();

        mockMvc.perform(get("/api/public/academy/subjects/" + testSubject.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.nameEn").value("Mathematics"))
            .andExpect(jsonPath("$.data.nameUrdu").value("ریاضی"));
    }

    @Test
    void testGetNonExistentClass() throws Exception {
        mockMvc.perform(get("/api/public/academy/classes/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetNonExistentSubject() throws Exception {
        mockMvc.perform(get("/api/public/academy/subjects/99999"))
            .andExpect(status().isNotFound());
    }
}
