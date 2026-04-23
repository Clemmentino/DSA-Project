-- =============================================
-- Seed Data: Default users
-- =============================================

-- Default admin account (username: admin, password: admin123)
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN');;

-- Default teacher account (username: teacher, password: teacher123)
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('teacher', 'teacher123', 'Juan Dela Cruz', 'TEACHER');;

-- Second teacher
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('teacher2', 'teacher123', 'Maria Santos', 'TEACHER');;
