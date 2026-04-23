package com.gradesecure.dto;

import java.math.BigDecimal;
import java.util.List;

public class SortResponse {
    private List<StudentGrade> sortedStudents;
    private boolean bonusApplied;
    private BigDecimal bonusAmount;

    public SortResponse() {}

    public SortResponse(List<StudentGrade> sortedStudents, boolean bonusApplied, BigDecimal bonusAmount) {
        this.sortedStudents = sortedStudents;
        this.bonusApplied = bonusApplied;
        this.bonusAmount = bonusAmount;
    }

    public List<StudentGrade> getSortedStudents() { return sortedStudents; }
    public void setSortedStudents(List<StudentGrade> sortedStudents) { this.sortedStudents = sortedStudents; }

    public boolean isBonusApplied() { return bonusApplied; }
    public void setBonusApplied(boolean bonusApplied) { this.bonusApplied = bonusApplied; }

    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }

    public static class StudentGrade {
        private Long studentId;
        private String fullName;
        private BigDecimal originalGrade;
        private BigDecimal finalGrade;

        public StudentGrade() {}

        public StudentGrade(Long studentId, String fullName, BigDecimal originalGrade, BigDecimal finalGrade) {
            this.studentId = studentId;
            this.fullName = fullName;
            this.originalGrade = originalGrade;
            this.finalGrade = finalGrade;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public BigDecimal getOriginalGrade() { return originalGrade; }
        public void setOriginalGrade(BigDecimal originalGrade) { this.originalGrade = originalGrade; }

        public BigDecimal getFinalGrade() { return finalGrade; }
        public void setFinalGrade(BigDecimal finalGrade) { this.finalGrade = finalGrade; }
    }
}
