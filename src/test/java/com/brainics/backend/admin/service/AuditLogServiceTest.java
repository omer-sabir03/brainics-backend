package com.brainics.backend.admin.service;

import com.brainics.backend.admin.dto.AuditLogResponse;
import com.brainics.backend.admin.model.AuditLog;
import com.brainics.backend.admin.repository.AuditLogRepository;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog testLog;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = AppUser.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.com")
                .build();

        testLog = AuditLog.builder()
                .id(1L)
                .actor(testUser)
                .action("CREATE")
                .resourceType("USER")
                .resourceId(2L)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .status("SUCCESS")
                .build();
    }

    @Test
    void testLogActionSuccess() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

        AuditLog result = auditLogService.logAction(1L, "CREATE", "USER", 2L, null, null, "SUCCESS");

        assertNotNull(result);
        assertEquals("CREATE", result.getAction());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testGetLogByIdSuccess() {
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

        var response = auditLogService.getLogById(1L);

        assertNotNull(response);
        assertEquals("CREATE", response.getAction());
        assertEquals("admin", response.getActorUsername());
    }

    @Test
    void testGetLogByIdNotFound() {
        when(auditLogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> auditLogService.getLogById(999L));
    }

    @Test
    void testGetAllLogsSuccess() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(testLog));
        when(auditLogRepository.findAll(pageable)).thenReturn(page);

        Page<AuditLogResponse> result = auditLogService.getAllLogs(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetLogsByActorSuccess() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(testLog));
        when(auditLogRepository.findByActorId(1L, pageable)).thenReturn(page);

        Page<AuditLogResponse> result = auditLogService.getLogsByActor(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetLogsByActionSuccess() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(testLog));
        when(auditLogRepository.findByAction("CREATE", pageable)).thenReturn(page);

        Page<AuditLogResponse> result = auditLogService.getLogsByAction("CREATE", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetLogsBetweenDatesSuccess() {
        ZonedDateTime startDate = ZonedDateTime.now().minusDays(7);
        ZonedDateTime endDate = ZonedDateTime.now();
        List<AuditLog> logs = Arrays.asList(testLog);
        when(auditLogRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(logs);

        List<AuditLogResponse> result = auditLogService.getLogsBetweenDates(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFailedActionsSuccess() {
        AuditLog failedLog = AuditLog.builder()
                .id(2L)
                .actor(testUser)
                .action("CREATE")
                .resourceType("USER")
                .status("FAILED")
                .errorMessage("User creation failed")
                .build();

        when(auditLogRepository.findAll()).thenReturn(Arrays.asList(testLog, failedLog));

        List<AuditLogResponse> result = auditLogService.getFailedActions(10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
