package com.gradesecure.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classrooms")
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    // ★ ADS: Foreign Key with SET NULL on teacher deletion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(name = "fk_classroom_teacher"))
    private User teacher;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ★ ADS: CASCADE — students deleted when class is deleted
    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    // ★ ADS: CASCADE — activities deleted when class is deleted
    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activities = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public ClassRoom() {}

    public ClassRoom(String name, User teacher) {
        this.name = name;
        this.teacher = teacher;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getTeacher() { return teacher; }
    public void setTeacher(User teacher) { this.teacher = teacher; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public List<Activity> getActivities() { return activities; }
    public void setActivities(List<Activity> activities) { this.activities = activities; }
}
