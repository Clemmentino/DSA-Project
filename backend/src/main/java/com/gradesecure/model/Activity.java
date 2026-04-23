package com.gradesecure.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    // ★ ADS: Foreign Key with CASCADE on class deletion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_activity_classroom"))
    private ClassRoom classroom;

    @NotNull
    @Positive
    @Column(name = "max_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxScore;

    // ★ ADS: CASCADE — grades deleted when activity is deleted
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();

    // Constructors
    public Activity() {}

    public Activity(String name, ClassRoom classroom, BigDecimal maxScore) {
        this.name = name;
        this.classroom = classroom;
        this.maxScore = maxScore;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ClassRoom getClassroom() { return classroom; }
    public void setClassroom(ClassRoom classroom) { this.classroom = classroom; }

    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }

    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
}
