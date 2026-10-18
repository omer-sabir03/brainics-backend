package com.brainics.backend.academy.repository;

import com.brainics.backend.academy.model.Subject;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends CrudRepository<Subject, Long> {
    Optional<Subject> findByNameEnIgnoreCase(String nameEn);
    List<Subject> findAllByIsActiveTrue();
    List<Subject> findAllByIsActiveTrueOrderByNameEn();
}
