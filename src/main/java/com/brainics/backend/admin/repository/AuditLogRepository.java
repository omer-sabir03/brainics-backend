package com.brainics.backend.admin.repository;

import com.brainics.backend.admin.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<AuditLog> findByActorId(Long actorId, Pageable pageable);
    Page<AuditLog> findByActorIdOrderByCreatedAtDesc(Long actorId, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
    Page<AuditLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);
    Page<AuditLog> findByResourceType(String resourceType, Pageable pageable);
    Page<AuditLog> findByResourceTypeOrderByCreatedAtDesc(String resourceType, Pageable pageable);
    Page<AuditLog> findAllByResourceIdOrderByCreatedAtDesc(Long resourceId, Pageable pageable);
    List<AuditLog> findByCreatedAtBetween(ZonedDateTime startDate, ZonedDateTime endDate);
}
