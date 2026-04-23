package com.gradesecure.repository;

import com.gradesecure.model.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    List<ClassRoom> findByTeacherId(Long teacherId);
}
