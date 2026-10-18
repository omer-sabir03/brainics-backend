package com.brainics.backend.academy.repository;

import com.brainics.backend.academy.model.Class;
import com.brainics.backend.academy.model.ClassSubject;
import com.brainics.backend.academy.model.Subject;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassSubjectRepository extends CrudRepository<ClassSubject, Long> {
    List<ClassSubject> findByClassEntityAndIsAvailableTrue(Class classEntity);
    List<ClassSubject> findByClassEntity(Class classEntity);
    Optional<ClassSubject> findByClassEntityAndSubject(Class classEntity, Subject subject);
    List<ClassSubject> findBySubject(Subject subject);
}
