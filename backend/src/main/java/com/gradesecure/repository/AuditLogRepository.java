package com.gradesecure.repository;

import com.gradesecure.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTableNameOrderByPerformedAtDesc(String tableName);
    List<AuditLog> findAllByOrderByPerformedAtDesc();
}
