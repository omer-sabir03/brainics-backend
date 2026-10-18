package com.brainics.backend.teacher.repository;

import com.brainics.backend.teacher.model.TeacherProfile;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends CrudRepository<TeacherProfile, Long> {
    Optional<TeacherProfile> findByUserId(Long userId);
    List<TeacherProfile> findAll();
}
