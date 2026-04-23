package com.gradesecure.controller;

import com.gradesecure.model.AuditLog;
import com.gradesecure.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * GET /api/audit-logs
     * ★ ADS: View audit logs populated by MySQL Triggers
     */
    @GetMapping
    public ResponseEntity<?> getAuditLogs(@RequestParam(required = false) String table) {
        List<AuditLog> logs;
        if (table != null && !table.isEmpty()) {
            logs = auditLogRepository.findByTableNameOrderByPerformedAtDesc(table);
        } else {
            logs = auditLogRepository.findAllByOrderByPerformedAtDesc();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (AuditLog log : logs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", log.getId());
            map.put("tableName", log.getTableName());
            map.put("actionType", log.getActionType());
            map.put("recordId", log.getRecordId());
            map.put("oldValues", log.getOldValues());
            map.put("newValues", log.getNewValues());
            map.put("performedAt", log.getPerformedAt());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }
}
