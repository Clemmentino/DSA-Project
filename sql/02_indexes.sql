-- =============================================
-- ★ ADS: Indexes (Indexes & Views Lesson)
-- =============================================

-- Regular index: speed up lookups by teacher
CREATE INDEX idx_classrooms_teacher ON classrooms(teacher_id);;

-- Regular index: speed up student lookups by class
CREATE INDEX idx_students_classroom ON students(classroom_id);;

-- Regular index: speed up grade lookups
CREATE INDEX idx_grades_student ON grades(student_id);;
CREATE INDEX idx_grades_activity ON grades(activity_id);;
