-- =============================================
-- ★ ADS: Triggers (Weeks 1-3 Lesson)
-- =============================================

-- BEFORE INSERT: Validate score doesn't exceed max
DROP TRIGGER IF EXISTS trg_grades_before_insert;;
CREATE TRIGGER trg_grades_before_insert
BEFORE INSERT ON grades
FOR EACH ROW
BEGIN
    DECLARE v_max_score DECIMAL(5,2);
    SELECT max_score INTO v_max_score FROM activities WHERE id = NEW.activity_id;
    IF NEW.score IS NOT NULL AND NEW.score > v_max_score THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Score exceeds maximum allowed for this activity';
    END IF;
    IF NEW.score IS NOT NULL AND NEW.score < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Score cannot be negative';
    END IF;
END;;

-- BEFORE UPDATE: Validate updated score
DROP TRIGGER IF EXISTS trg_grades_before_update;;
CREATE TRIGGER trg_grades_before_update
BEFORE UPDATE ON grades
FOR EACH ROW
BEGIN
    DECLARE v_max_score DECIMAL(5,2);
    SELECT max_score INTO v_max_score FROM activities WHERE id = NEW.activity_id;
    IF NEW.score IS NOT NULL AND NEW.score > v_max_score THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Score exceeds maximum allowed for this activity';
    END IF;
    IF NEW.score IS NOT NULL AND NEW.score < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Score cannot be negative';
    END IF;
END;;

-- AFTER INSERT: Log grade insertion to audit_logs
DROP TRIGGER IF EXISTS trg_grades_after_insert;;
CREATE TRIGGER trg_grades_after_insert
AFTER INSERT ON grades
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (table_name, action_type, record_id, new_values)
    VALUES ('grades', 'INSERT', NEW.id,
        JSON_OBJECT('student_id', NEW.student_id, 'activity_id', NEW.activity_id, 'score', NEW.score));
END;;

-- AFTER UPDATE: Log grade changes to audit_logs
DROP TRIGGER IF EXISTS trg_grades_after_update;;
CREATE TRIGGER trg_grades_after_update
AFTER UPDATE ON grades
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (table_name, action_type, record_id, old_values, new_values)
    VALUES ('grades', 'UPDATE', NEW.id,
        JSON_OBJECT('score', OLD.score),
        JSON_OBJECT('score', NEW.score));
END;;

-- AFTER DELETE: Log grade deletion to audit_logs
DROP TRIGGER IF EXISTS trg_grades_after_delete;;
CREATE TRIGGER trg_grades_after_delete
AFTER DELETE ON grades
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (table_name, action_type, record_id, old_values)
    VALUES ('grades', 'DELETE', OLD.id,
        JSON_OBJECT('student_id', OLD.student_id, 'activity_id', OLD.activity_id, 'score', OLD.score));
END;;
