-- Seed data loaded by Spring Boot on startup
-- Default admin account
INSERT INTO users (username, password, full_name, role)
SELECT 'admin', 'admin123', 'System Administrator', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Default teacher accounts
INSERT INTO users (username, password, full_name, role)
SELECT 'teacher', 'teacher123', 'Juan Dela Cruz', 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'teacher');

INSERT INTO users (username, password, full_name, role)
SELECT 'teacher2', 'teacher123', 'Maria Santos', 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'teacher2');
