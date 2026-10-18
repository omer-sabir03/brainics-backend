package com.brainics.backend.academy.repository;

import com.brainics.backend.academy.model.Class;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends CrudRepository<Class, Long> {
    Optional<Class> findByClassNumber(Integer classNumber);
    List<Class> findAllByIsActiveTrue();
    List<Class> findAllByIsActiveTrueOrderByClassNumber();
}
