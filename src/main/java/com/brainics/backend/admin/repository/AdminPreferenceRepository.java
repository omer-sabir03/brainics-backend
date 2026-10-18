package com.brainics.backend.admin.repository;

import com.brainics.backend.admin.model.AdminPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminPreferenceRepository extends JpaRepository<AdminPreference, Long> {
    Optional<AdminPreference> findByUserId(Long userId);
}
