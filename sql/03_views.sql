-- =============================================
-- ★ ADS: Views (Indexes & Views Lesson)
-- =============================================

-- View 1: Class summary with student count and teacher name
CREATE OR REPLACE VIEW vw_class_summary AS
SELECT
    c.id AS class_id,
    c.name AS class_name,
    u.full_name AS teacher_name,
    COUNT(s.id) AS student_count
FROM classrooms c
LEFT JOIN users u ON c.teacher_id = u.id
LEFT JOIN students s ON s.classroom_id = c.id
GROUP BY c.id, c.name, u.full_name;;

-- View 2: Student grade report (average percentage per student)
CREATE OR REPLACE VIEW vw_student_grades AS
SELECT
    s.id AS student_id,
    s.full_name AS student_name,
    c.name AS class_name,
    ROUND(AVG(g.score / a.max_score * 100), 2) AS average_grade
FROM students s
JOIN classrooms c ON s.classroom_id = c.id
LEFT JOIN grades g ON g.student_id = s.id
LEFT JOIN activities a ON g.activity_id = a.id
GROUP BY s.id, s.full_name, c.name;;

-- View 3: Activity overview per class
CREATE OR REPLACE VIEW vw_activity_overview AS
SELECT
    a.id AS activity_id,
    a.name AS activity_name,
    c.name AS class_name,
    a.max_score,
    COUNT(g.id) AS grades_entered,
    ROUND(AVG(g.score), 2) AS avg_score
FROM activities a
JOIN classrooms c ON a.classroom_id = c.id
LEFT JOIN grades g ON g.activity_id = a.id
GROUP BY a.id, a.name, c.name, a.max_score;;
