package com.gradesecure.service;

import com.gradesecure.model.ClassRoom;
import com.gradesecure.model.Student;
import com.gradesecure.model.User;
import com.gradesecure.repository.ClassRoomRepository;
import com.gradesecure.repository.StudentRepository;
import com.gradesecure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassService {

    private final ClassRoomRepository classRoomRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public ClassService(ClassRoomRepository classRoomRepository,
                        StudentRepository studentRepository,
                        UserRepository userRepository) {
        this.classRoomRepository = classRoomRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public List<ClassRoom> getAllClasses() {
        return classRoomRepository.findAll();
    }

    public List<ClassRoom> getClassesByTeacher(Long teacherId) {
        return classRoomRepository.findByTeacherId(teacherId);
    }

    public ClassRoom getClassById(Long id) {
        return classRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));
    }

    /**
     * Create a new class — maps to Flowchart 1 (Class Creation):
     * input class info → create class
     */
    @Transactional // ★ ADS: Transaction
    public ClassRoom createClass(String name, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        ClassRoom classRoom = new ClassRoom(name, teacher);
        return classRoomRepository.save(classRoom);
    }

    @Transactional
    public void deleteClass(Long id) {
        // ★ ADS: CASCADE will auto-delete students, activities, grades
        classRoomRepository.deleteById(id);
    }

    /**
     * Add student to roster — maps to Flowchart 1:
     * input student info → more students? loop
     */
    @Transactional
    public Student addStudent(Long classId, String fullName) {
        ClassRoom classRoom = getClassById(classId);
        Student student = new Student(fullName, classRoom);
        return studentRepository.save(student);
    }

    public List<Student> getStudents(Long classId) {
        return studentRepository.findByClassroomIdOrderByFullNameAsc(classId);
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        // ★ ADS: CASCADE will auto-delete grades for this student
        studentRepository.deleteById(studentId);
    }
}
