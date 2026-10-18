package com.brainics.backend.academy.repository;

import com.brainics.backend.academy.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByClassNumber(Short classNumber);
    List<Class> findAllByIsActiveTrue();
    List<Class> findAllByIsActiveTrueOrderByClassNumber();
}
