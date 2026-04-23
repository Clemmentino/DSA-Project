package com.gradesecure.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    // ★ ADS: Foreign Key with CASCADE on class deletion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_student_classroom"))
    private ClassRoom classroom;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ★ ADS: CASCADE — grades deleted when student is deleted
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public Student() {}

    public Student(String fullName, ClassRoom classroom) {
        this.fullName = fullName;
        this.classroom = classroom;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public ClassRoom getClassroom() { return classroom; }
    public void setClassroom(ClassRoom classroom) { this.classroom = classroom; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
}
