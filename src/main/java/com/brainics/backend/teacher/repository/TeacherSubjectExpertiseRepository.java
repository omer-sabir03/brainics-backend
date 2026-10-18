package com.brainics.backend.teacher.repository;

import com.brainics.backend.teacher.model.TeacherSubjectExpertise;
import org.springframework.data.jpa.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSubjectExpertiseRepository extends CrudRepository<TeacherSubjectExpertise, Long> {
    List<TeacherSubjectExpertise> findByTeacherId(Long teacherId);
}
