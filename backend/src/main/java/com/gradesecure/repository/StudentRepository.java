package com.gradesecure.repository;

import com.gradesecure.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByClassroomId(Long classroomId);
    List<Student> findByClassroomIdOrderByFullNameAsc(Long classroomId);
}
