package com.brainics.backend.teacher.repository;

import com.brainics.backend.teacher.model.TeacherSubjectExpertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSubjectExpertiseRepository extends JpaRepository<TeacherSubjectExpertise, Long> {
    List<TeacherSubjectExpertise> findByTeacherId(Long teacherId);
    void deleteByTeacherId(Long teacherId);
    List<TeacherSubjectExpertise> findBySubjectId(Long subjectId);
    List<TeacherSubjectExpertise> findByProficiencyLevel(String proficiencyLevel);
}
