package com.brainics.backend.admin.controller;

import com.brainics.backend.admin.dto.AuditLogResponse;
import com.brainics.backend.admin.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{logId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLogResponse> getLogById(@PathVariable Long logId) {
        log.info("Fetching audit log: {}", logId);
        AuditLogResponse response = auditLogService.getLogById(logId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getAllLogs(Pageable pageable) {
        log.info("Fetching all audit logs: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AuditLogResponse> response = auditLogService.getAllLogs(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/actor/{actorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getLogsByActor(
            @PathVariable Long actorId,
            Pageable pageable) {
        log.info("Fetching audit logs for actor: {}", actorId);
        Page<AuditLogResponse> response = auditLogService.getLogsByActor(actorId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getLogsByAction(
            @PathVariable String action,
            Pageable pageable) {
        log.info("Fetching audit logs for action: {}", action);
        Page<AuditLogResponse> response = auditLogService.getLogsByAction(action, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resource/{resourceType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getLogsByResourceType(
            @PathVariable String resourceType,
            Pageable pageable) {
        log.info("Fetching audit logs for resource type: {}", resourceType);
        Page<AuditLogResponse> response = auditLogService.getLogsByResourceType(resourceType, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resource-id/{resourceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponse>> getLogsByResourceId(
            @PathVariable Long resourceId,
            Pageable pageable) {
        log.info("Fetching audit logs for resource ID: {}", resourceId);
        Page<AuditLogResponse> response = auditLogService.getLogsByResourceId(resourceId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getLogsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        log.info("Fetching audit logs between {} and {}", startDate, endDate);
        List<AuditLogResponse> response = auditLogService.getLogsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getFailedActions(
            @RequestParam(defaultValue = "100") int limit) {
        log.info("Fetching failed audit logs: limit={}", limit);
        List<AuditLogResponse> response = auditLogService.getFailedActions(limit);
        return ResponseEntity.ok(response);
    }
}
