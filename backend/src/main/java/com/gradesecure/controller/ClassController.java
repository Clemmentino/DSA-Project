package com.gradesecure.controller;

import com.gradesecure.model.ClassRoom;
import com.gradesecure.model.Student;
import com.gradesecure.service.ClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    /**
     * GET /api/classes — List all classes
     * Flowchart 1: Select existing class
     */
    @GetMapping
    public ResponseEntity<?> getAllClasses(@RequestParam(required = false) Long teacherId) {
        List<ClassRoom> classes;
        if (teacherId != null) {
            classes = classService.getClassesByTeacher(teacherId);
        } else {
            classes = classService.getAllClasses();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ClassRoom c : classes) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("teacherId", c.getTeacher() != null ? c.getTeacher().getId() : null);
            map.put("teacherName", c.getTeacher() != null ? c.getTeacher().getFullName() : null);
            map.put("studentCount", c.getStudents().size());
            map.put("createdAt", c.getCreatedAt());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/classes — Create a new class
     * Flowchart 1: input class info
     */
    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        Long teacherId = Long.valueOf(body.get("teacherId").toString());
        ClassRoom created = classService.createClass(name, teacherId);
        return ResponseEntity.ok(Map.of(
            "id", created.getId(),
            "name", created.getName(),
            "message", "Class created successfully"
        ));
    }

    /**
     * DELETE /api/classes/{id}
     * ★ ADS: CASCADE deletes students, activities, and grades
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(Map.of("message", "Class deleted successfully"));
    }

    /**
     * GET /api/classes/{id}/students — Get class roster
     * Flowchart 1: Open class roster
     */
    @GetMapping("/{id}/students")
    public ResponseEntity<?> getStudents(@PathVariable Long id) {
        List<Student> students = classService.getStudents(id);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student s : students) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", s.getId());
            map.put("fullName", s.getFullName());
            map.put("createdAt", s.getCreatedAt());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/classes/{id}/students — Add student to roster
     * Flowchart 1: input student info → more students? → loop
     */
    @PostMapping("/{id}/students")
    public ResponseEntity<?> addStudent(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        Student student = classService.addStudent(id, fullName);
        return ResponseEntity.ok(Map.of(
            "id", student.getId(),
            "fullName", student.getFullName(),
            "message", "Student added successfully"
        ));
    }

    /**
     * DELETE /api/classes/students/{studentId}
     * ★ ADS: CASCADE deletes all grades for this student
     */
    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long studentId) {
        classService.deleteStudent(studentId);
        return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));
    }
}
