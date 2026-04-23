package com.gradesecure.service;

import com.gradesecure.model.Activity;
import com.gradesecure.model.ClassRoom;
import com.gradesecure.model.Grade;
import com.gradesecure.model.Student;
import com.gradesecure.repository.ActivityRepository;
import com.gradesecure.repository.ClassRoomRepository;
import com.gradesecure.repository.GradeRepository;
import com.gradesecure.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final ClassRoomRepository classRoomRepository;

    public GradeService(GradeRepository gradeRepository,
                        StudentRepository studentRepository,
                        ActivityRepository activityRepository,
                        ClassRoomRepository classRoomRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        this.classRoomRepository = classRoomRepository;
    }

    /**
     * Add or update an activity column — maps to Flowchart 2 (Spreadsheet):
     * Modify activity? → Yes → Edit activity columns
     */
    @Transactional
    public Activity addActivity(Long classId, String name, BigDecimal maxScore) {
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Activity activity = new Activity(name, classRoom, maxScore);
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity updateActivity(Long activityId, String name, BigDecimal maxScore) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        activity.setName(name);
        activity.setMaxScore(maxScore);
        return activityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    public List<Activity> getActivities(Long classId) {
        return activityRepository.findByClassroomIdOrderByIdAsc(classId);
    }

    /**
     * Save a grade to a cell — maps to Flowchart 2 (Spreadsheet):
     * Input grade? → Yes → Save grade to cell
     * ★ ADS: Triggers will fire automatically to log this in audit_logs
     */
    @Transactional // ★ ADS: Transaction — ensures atomicity
    public Grade saveGrade(Long studentId, Long activityId, BigDecimal score) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // Validate score against max (also enforced by BEFORE INSERT trigger)
        if (score != null && score.compareTo(activity.getMaxScore()) > 0) {
            throw new RuntimeException("Score exceeds maximum of " + activity.getMaxScore());
        }
        if (score != null && score.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Score cannot be negative");
        }

        Optional<Grade> existing = gradeRepository.findByStudentIdAndActivityId(studentId, activityId);

        if (existing.isPresent()) {
            Grade grade = existing.get();
            grade.setScore(score);
            return gradeRepository.save(grade);
        } else {
            Grade grade = new Grade(student, activity, score);
            return gradeRepository.save(grade);
        }
    }

    /**
     * Get full spreadsheet data: students × activities with scores.
     */
    public Map<String, Object> getSpreadsheet(Long classId) {
        List<Student> students = studentRepository.findByClassroomIdOrderByFullNameAsc(classId);
        List<Activity> activities = activityRepository.findByClassroomIdOrderByIdAsc(classId);
        List<Grade> allGrades = gradeRepository.findByClassroomId(classId);

        // Build a map: studentId -> activityId -> score
        Map<Long, Map<Long, BigDecimal>> gradeMap = new HashMap<>();
        for (Grade g : allGrades) {
            gradeMap
                .computeIfAbsent(g.getStudent().getId(), k -> new HashMap<>())
                .put(g.getActivity().getId(), g.getScore());
        }

        // Build response
        List<Map<String, Object>> studentRows = new ArrayList<>();
        for (Student s : students) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("studentId", s.getId());
            row.put("fullName", s.getFullName());

            Map<Long, BigDecimal> studentGrades = gradeMap.getOrDefault(s.getId(), Collections.emptyMap());
            List<Map<String, Object>> scores = new ArrayList<>();
            BigDecimal totalPercent = BigDecimal.ZERO;
            int gradedCount = 0;

            for (Activity a : activities) {
                Map<String, Object> cell = new HashMap<>();
                cell.put("activityId", a.getId());
                BigDecimal score = studentGrades.get(a.getId());
                cell.put("score", score);
                scores.add(cell);

                if (score != null) {
                    BigDecimal percent = score.divide(a.getMaxScore(), 4, RoundingMode.HALF_UP)
                                              .multiply(BigDecimal.valueOf(100));
                    totalPercent = totalPercent.add(percent);
                    gradedCount++;
                }
            }
            row.put("scores", scores);
            row.put("average", gradedCount > 0
                    ? totalPercent.divide(BigDecimal.valueOf(gradedCount), 2, RoundingMode.HALF_UP)
                    : null);
            studentRows.add(row);
        }

        List<Map<String, Object>> activityList = new ArrayList<>();
        for (Activity a : activities) {
            Map<String, Object> act = new HashMap<>();
            act.put("id", a.getId());
            act.put("name", a.getName());
            act.put("maxScore", a.getMaxScore());
            activityList.add(act);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("activities", activityList);
        result.put("students", studentRows);
        return result;
    }

    /**
     * Export CSV — maps to Flowchart 2: Export? → Yes → Generate report file
     */
    public String exportCsv(Long classId) {
        Map<String, Object> data = getSpreadsheet(classId);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> activities = (List<Map<String, Object>>) data.get("activities");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) data.get("students");

        StringBuilder csv = new StringBuilder();

        // Header row
        csv.append("Student Name");
        for (Map<String, Object> a : activities) {
            csv.append(",").append(a.get("name")).append(" (").append(a.get("maxScore")).append(")");
        }
        csv.append(",Average\n");

        // Data rows
        for (Map<String, Object> s : students) {
            csv.append(s.get("fullName"));
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> scores = (List<Map<String, Object>>) s.get("scores");
            for (Map<String, Object> score : scores) {
                csv.append(",").append(score.get("score") != null ? score.get("score") : "");
            }
            csv.append(",").append(s.get("average") != null ? s.get("average") : "");
            csv.append("\n");
        }

        return csv.toString();
    }
}
