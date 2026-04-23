package com.gradesecure.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ★ ADS: Audit log table — populated automatically by MySQL triggers,
 * NOT by application code. This entity is read-only from the app's perspective.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false, length = 50)
    private String tableName;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "old_values", columnDefinition = "JSON")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "JSON")
    private String newValues;

    @Column(name = "performed_at", updatable = false)
    private LocalDateTime performedAt;

    public enum ActionType {
        INSERT, UPDATE, DELETE
    }

    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }

    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }

    public LocalDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }
}
