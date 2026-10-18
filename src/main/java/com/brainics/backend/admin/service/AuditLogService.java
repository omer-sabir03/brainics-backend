package com.brainics.backend.admin.service;

import com.brainics.backend.admin.dto.AuditLogResponse;
import com.brainics.backend.admin.model.AuditLog;
import com.brainics.backend.admin.repository.AuditLogRepository;
import com.brainics.backend.auth.model.AppUser;
import com.brainics.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLog logAction(Long actorId, String action, String resourceType, Long resourceId,
                              Map<String, Object> oldValues, Map<String, Object> newValues, String status) {
        return logAction(actorId, action, resourceType, resourceId, oldValues, newValues, status, null);
    }

    public AuditLog logAction(Long actorId, String action, String resourceType, Long resourceId,
                              Map<String, Object> oldValues, Map<String, Object> newValues,
                              String status, String errorMessage) {
        String ipAddress = getClientIp();
        String userAgent = getUserAgent();

        AuditLog log = AuditLog.builder()
                .actorId(actorId)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .oldValues(oldValues)
                .newValues(newValues)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(status)
                .errorMessage(errorMessage)
                .build();

        AuditLog saved = auditLogRepository.save(log);
        log.info("Action logged: {} {} by user {} - Status: {}", action, resourceType, actorId, status);
        return saved;
    }

    @Transactional(readOnly = true)
    public AuditLogResponse getLogById(Long logId) {
        AuditLog log = auditLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Audit log not found: " + logId));
        return toResponse(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByActor(Long actorId, Pageable pageable) {
        return auditLogRepository.findByActorId(actorId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByResourceType(String resourceType, Pageable pageable) {
        return auditLogRepository.findByResourceType(resourceType, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getLogsByResourceId(Long resourceId, Pageable pageable) {
        return auditLogRepository.findAllByResourceIdOrderByCreatedAtDesc(resourceId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsBetweenDates(ZonedDateTime startDate, ZonedDateTime endDate) {
        return auditLogRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getFailedActions(int limit) {
        return auditLogRepository.findAll().stream()
                .filter(log -> "FAILED".equals(log.getStatus()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse toResponse(AuditLog log) {
        String actorUsername = null;
        if (log.getActorId() != null) {
            actorUsername = userRepository.findById(log.getActorId())
                    .map(AppUser::getUsername)
                    .orElse("Unknown");
        }

        return AuditLogResponse.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorUsername(actorUsername)
                .action(log.getAction())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .oldValues(log.getOldValues())
                .newValues(log.getNewValues())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .status(log.getStatus())
                .errorMessage(log.getErrorMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "UNKNOWN";

            HttpServletRequest request = attrs.getRequest();
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0];
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            log.warn("Unable to get client IP", e);
            return "UNKNOWN";
        }
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "UNKNOWN";
            return attrs.getRequest().getHeader("User-Agent");
        } catch (Exception e) {
            log.warn("Unable to get user agent", e);
            return "UNKNOWN";
        }
    }
}
