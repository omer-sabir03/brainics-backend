package com.brainics.backend.admin.service;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import com.brainics.backend.enquiry.model.AdmissionEnquiry;
import com.brainics.backend.enquiry.model.EnquiryStatusHistory;
import com.brainics.backend.enquiry.repository.AdmissionEnquiryRepository;
import com.brainics.backend.enquiry.repository.EnquiryStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnquiryStatusServiceTest {

    @Mock
    private AdmissionEnquiryRepository enquiryRepository;

    @Mock
    private EnquiryStatusHistoryRepository statusHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private EnquiryStatusService enquiryStatusService;

    private AdmissionEnquiry testEnquiry;
    private AppUser testUser;
    private Class testClass;

    @BeforeEach
    void setUp() {
        testClass = Class.builder()
                .id(1L)
                .classNumber((short) 10)
                .description("10th Grade")
                .build();

        testUser = AppUser.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.com")
                .build();

        testEnquiry = AdmissionEnquiry.builder()
                .id(1L)
                .parentName("Ahmed Khan")
                .parentEmail("ahmed@test.com")
                .parentPhone("+923001234567")
                .studentName("Ali Khan")
                .classEntity(testClass)
                .status("NEW")
                .source("WEB")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    @Test
    void testUpdateEnquiryStatusSuccess() {
        when(enquiryRepository.findById(1L)).thenReturn(Optional.of(testEnquiry));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(statusHistoryRepository.save(any(EnquiryStatusHistory.class))).thenReturn(null);
        when(enquiryRepository.save(any(AdmissionEnquiry.class))).thenReturn(testEnquiry);

        enquiryStatusService.updateEnquiryStatus(1L, "CONTACTED", "Called parent", "Follow-up scheduled", 1L);

        verify(statusHistoryRepository, times(1)).save(any(EnquiryStatusHistory.class));
        verify(enquiryRepository, times(1)).save(any(AdmissionEnquiry.class));
        verify(auditLogService, times(1)).logAction(anyLong(), eq("UPDATE"), eq("ENQUIRY_STATUS"), anyLong(), any(), any(), eq("SUCCESS"));
    }

    @Test
    void testUpdateEnquiryStatusNotFound() {
        when(enquiryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () ->
                enquiryStatusService.updateEnquiryStatus(999L, "CONTACTED", "Called", "Notes", 1L));
    }

    @Test
    void testGetStatusHistorySuccess() {
        EnquiryStatusHistory history = EnquiryStatusHistory.builder()
                .id(1L)
                .enquiry(testEnquiry)
                .oldStatus("NEW")
                .newStatus("CONTACTED")
                .changedBy(testUser)
                .build();

        when(statusHistoryRepository.findByEnquiryIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(history));

        List<EnquiryStatusHistory> result = enquiryStatusService.getStatusHistory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CONTACTED", result.get(0).getNewStatus());
    }

    @Test
    void testGetConversionRateSuccess() {
        AdmissionEnquiry enrolled = AdmissionEnquiry.builder()
                .id(2L)
                .status("ENROLLED")
                .classEntity(testClass)
                .parentName("Parent")
                .parentEmail("p@test.com")
                .parentPhone("+923001234567")
                .studentName("Student")
                .build();

        when(enquiryRepository.findAll()).thenReturn(Arrays.asList(testEnquiry, enrolled));

        Double rate = enquiryStatusService.getConversionRate();

        assertNotNull(rate);
        assertEquals(50.0, rate);
    }

    @Test
    void testGetUnrespondenEnquiriesSuccess() {
        when(enquiryRepository.findAll()).thenReturn(Arrays.asList(testEnquiry));

        List<AdmissionEnquiry> result = enquiryStatusService.getUnrespondenEnquiries();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NEW", result.get(0).getStatus());
    }

    @Test
    void testCountEnquiriesByStatusSuccess() {
        when(enquiryRepository.findAll()).thenReturn(Arrays.asList(testEnquiry));

        Long count = enquiryStatusService.countEnquiriesByStatus("NEW");

        assertEquals(1L, count);
    }

    @Test
    void testAssignEnquiryToTeacherSuccess() {
        when(enquiryRepository.findById(1L)).thenReturn(Optional.of(testEnquiry));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser));
        when(enquiryRepository.save(any(AdmissionEnquiry.class))).thenReturn(testEnquiry);

        enquiryStatusService.assignEnquiryToTeacher(1L, 2L, 1L);

        verify(enquiryRepository, times(1)).save(any(AdmissionEnquiry.class));
        verify(auditLogService, times(1)).logAction(anyLong(), eq("UPDATE"), eq("ENQUIRY_ASSIGNMENT"), anyLong(), any(), any(), eq("SUCCESS"));
    }
}
