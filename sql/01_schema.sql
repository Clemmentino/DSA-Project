-- =============================================
-- DSA-GradeSecure: Schema with FK Constraints
-- ★ ADS: Foreign Key Constraints (Finals Lesson 2)
-- =============================================

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'TEACHER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;;

-- Table: classrooms
-- ★ ADS: SET NULL on teacher deletion (class preserved, teacher unlinked)
CREATE TABLE IF NOT EXISTS classrooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    teacher_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_classroom_teacher FOREIGN KEY (teacher_id)
        REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;;

-- Table: students
-- ★ ADS: CASCADE on class deletion (students removed with class)
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    classroom_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_classroom FOREIGN KEY (classroom_id)
        REFERENCES classrooms(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;;

-- Table: activities
-- ★ ADS: CASCADE on class deletion
CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    classroom_id BIGINT NOT NULL,
    max_score DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_activity_classroom FOREIGN KEY (classroom_id)
        REFERENCES classrooms(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;;

-- Table: grades
-- ★ ADS: CASCADE on student/activity deletion
CREATE TABLE IF NOT EXISTS grades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    score DECIMAL(5,2) DEFAULT NULL,
    CONSTRAINT fk_grade_student FOREIGN KEY (student_id)
        REFERENCES students(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_grade_activity FOREIGN KEY (activity_id)
        REFERENCES activities(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_student_activity UNIQUE (student_id, activity_id)
) ENGINE=InnoDB;;

-- Table: audit_logs (populated by triggers)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    action_type ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    record_id BIGINT NOT NULL,
    old_values JSON DEFAULT NULL,
    new_values JSON DEFAULT NULL,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;;
