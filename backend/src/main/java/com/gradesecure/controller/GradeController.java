package com.gradesecure.controller;

import com.gradesecure.service.GradeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    /**
     * GET /api/classes/{id}/spreadsheet
     * Flowchart 2: Open spreadsheet → see all students × activities
     */
    @GetMapping("/{id}/spreadsheet")
    public ResponseEntity<?> getSpreadsheet(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getSpreadsheet(id));
    }

    /**
     * POST /api/classes/{id}/activities — Add activity column
     * Flowchart 2: Modify activity? → Yes → Edit activity columns
     */
    @PostMapping("/{id}/activities")
    public ResponseEntity<?> addActivity(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        BigDecimal maxScore = new BigDecimal(body.get("maxScore").toString());
        var activity = gradeService.addActivity(id, name, maxScore);
        return ResponseEntity.ok(Map.of(
            "id", activity.getId(),
            "name", activity.getName(),
            "maxScore", activity.getMaxScore(),
            "message", "Activity added successfully"
        ));
    }

    /**
     * PUT /api/classes/activities/{activityId} — Update activity
     */
    @PutMapping("/activities/{activityId}")
    public ResponseEntity<?> updateActivity(@PathVariable Long activityId, @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        BigDecimal maxScore = new BigDecimal(body.get("maxScore").toString());
        var activity = gradeService.updateActivity(activityId, name, maxScore);
        return ResponseEntity.ok(Map.of(
            "id", activity.getId(),
            "name", activity.getName(),
            "maxScore", activity.getMaxScore(),
            "message", "Activity updated successfully"
        ));
    }

    /**
     * DELETE /api/classes/activities/{activityId}
     * ★ ADS: CASCADE deletes all grades for this activity
     */
    @DeleteMapping("/activities/{activityId}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long activityId) {
        gradeService.deleteActivity(activityId);
        return ResponseEntity.ok(Map.of("message", "Activity deleted successfully"));
    }

    /**
     * PUT /api/grades — Save a grade to a cell
     * Flowchart 2: Input grade? → Yes → Save grade to cell
     * ★ ADS: Triggers fire automatically to log this in audit_logs
     */
    @PutMapping("/grades")
    public ResponseEntity<?> saveGrade(@RequestBody Map<String, Object> body) {
        Long studentId = Long.valueOf(body.get("studentId").toString());
        Long activityId = Long.valueOf(body.get("activityId").toString());
        BigDecimal score = body.get("score") != null
                ? new BigDecimal(body.get("score").toString())
                : null;
        try {
            var grade = gradeService.saveGrade(studentId, activityId, score);
            return ResponseEntity.ok(Map.of(
                "id", grade.getId(),
                "message", "Grade saved successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/classes/{id}/export — Export CSV report
     * Flowchart 2: Export? → Yes → Generate report file
     */
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportCsv(@PathVariable Long id) {
        String csv = gradeService.exportCsv(id);
        byte[] bytes = csv.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grades_report.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytes.length)
                .body(bytes);
    }
}
