package com.gradesecure.repository;

import com.gradesecure.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    Optional<Grade> findByStudentIdAndActivityId(Long studentId, Long activityId);

    @Query("SELECT g FROM Grade g WHERE g.student.classroom.id = :classId")
    List<Grade> findByClassroomId(@Param("classId") Long classId);
}
