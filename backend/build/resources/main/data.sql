-- Seed data loaded by Spring Boot on startup
-- Default admin account
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN');;

-- Default teacher accounts
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('teacher', 'teacher123', 'Juan Dela Cruz', 'TEACHER');;

INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('teacher2', 'teacher123', 'Maria Santos', 'TEACHER');;
