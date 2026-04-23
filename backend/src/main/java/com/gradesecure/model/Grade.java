package com.gradesecure.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "grades",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_student_activity",
           columnNames = {"student_id", "activity_id"}
       ))
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ★ ADS: Foreign Key with CASCADE on student deletion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_grade_student"))
    private Student student;

    // ★ ADS: Foreign Key with CASCADE on activity deletion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_grade_activity"))
    private Activity activity;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    // Constructors
    public Grade() {}

    public Grade(Student student, Activity activity, BigDecimal score) {
        this.student = student;
        this.activity = activity;
        this.score = score;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
}
