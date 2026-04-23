-- =============================================
-- ★ ADS: Stored Procedures (Stored Proc Lesson)
-- =============================================

-- SP 1: Compute final grade for a student (IN + OUT parameters)
DROP PROCEDURE IF EXISTS ComputeStudentGrade;;
CREATE PROCEDURE ComputeStudentGrade(
    IN p_student_id BIGINT,
    OUT p_final_grade DECIMAL(5,2)
)
BEGIN
    SELECT ROUND(AVG(g.score / a.max_score * 100), 2)
    INTO p_final_grade
    FROM grades g
    JOIN activities a ON g.activity_id = a.id
    WHERE g.student_id = p_student_id;
END;;

-- SP 2: Apply bonus marks to all students in a class
-- ★ ADS: Transaction (Finals Lesson 1) — START TRANSACTION, COMMIT, ROLLBACK
DROP PROCEDURE IF EXISTS ApplyBonusToClass;;
CREATE PROCEDURE ApplyBonusToClass(
    IN p_class_id BIGINT,
    IN p_bonus_amount DECIMAL(5,2),
    OUT p_students_affected INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_students_affected = -1;
    END;

    START TRANSACTION;

    UPDATE grades g
    JOIN students s ON g.student_id = s.id
    SET g.score = LEAST(g.score + p_bonus_amount,
                        (SELECT max_score FROM activities WHERE id = g.activity_id))
    WHERE s.classroom_id = p_class_id;

    SELECT ROW_COUNT() INTO p_students_affected;

    COMMIT;
END;;

-- SP 3: Get class statistics (demonstrates OUT parameters)
DROP PROCEDURE IF EXISTS GetClassStats;;
CREATE PROCEDURE GetClassStats(
    IN p_class_id BIGINT,
    OUT p_student_count INT,
    OUT p_activity_count INT,
    OUT p_avg_grade DECIMAL(5,2)
)
BEGIN
    SELECT COUNT(*) INTO p_student_count
    FROM students WHERE classroom_id = p_class_id;

    SELECT COUNT(*) INTO p_activity_count
    FROM activities WHERE classroom_id = p_class_id;

    SELECT ROUND(AVG(g.score / a.max_score * 100), 2) INTO p_avg_grade
    FROM grades g
    JOIN activities a ON g.activity_id = a.id
    JOIN students s ON g.student_id = s.id
    WHERE s.classroom_id = p_class_id;
END;;
