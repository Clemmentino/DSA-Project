package com.gradesecure.service;

import com.gradesecure.dto.SortResponse;
import com.gradesecure.dto.SortResponse.StudentGrade;
import com.gradesecure.model.Activity;
import com.gradesecure.model.Grade;
import com.gradesecure.model.Student;
import com.gradesecure.repository.ActivityRepository;
import com.gradesecure.repository.GradeRepository;
import com.gradesecure.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * ★ DSA: Radix Sort (LSD — Least Significant Digit) implementation.
 *
 * Implements Flowchart 4 exactly:
 * 1. Examine students with final grades
 * 2. Find max grade (determine digit count, d)
 * 3. Set exp = 1
 * 4. While max/exp > 0:
 *    a. Create 10 buckets (0-9)
 *    b. Distribute students: digit = (grade / exp) mod 10
 *    c. Collect buckets (0-9) — ascending order
 *    d. exp = exp * 10
 * 5. Apply bonus mark: grade + 10
 * 6. Return sorted roster
 */
@Service
public class RadixSortService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final ActivityRepository activityRepository;

    public RadixSortService(StudentRepository studentRepository,
                            GradeRepository gradeRepository,
                            ActivityRepository activityRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Sort students by their average grade using Radix Sort (LSD),
     * then apply a +10 bonus mark.
     */
    public SortResponse sortAndApplyBonus(Long classId) {
        List<Student> students = studentRepository.findByClassroomIdOrderByFullNameAsc(classId);
        List<Activity> activities = activityRepository.findByClassroomIdOrderByIdAsc(classId);

        if (students.isEmpty()) {
            return new SortResponse(Collections.emptyList(), false, BigDecimal.ZERO);
        }

        // Step 1: Examine students with final grades — compute averages
        List<StudentGrade> studentGrades = new ArrayList<>();
        for (Student s : students) {
            List<Grade> grades = gradeRepository.findByStudentId(s.getId());
            BigDecimal average = computeAverage(grades, activities);
            studentGrades.add(new StudentGrade(
                s.getId(), s.getFullName(), average, average
            ));
        }

        // Step 2-4: Radix Sort (LSD) on the integer part of grades
        radixSort(studentGrades);

        // Step 5: Apply bonus mark: grade + 10
        BigDecimal bonus = BigDecimal.TEN;
        for (StudentGrade sg : studentGrades) {
            if (sg.getOriginalGrade() != null) {
                BigDecimal finalGrade = sg.getOriginalGrade().add(bonus);
                // Cap at 100
                if (finalGrade.compareTo(BigDecimal.valueOf(100)) > 0) {
                    finalGrade = BigDecimal.valueOf(100);
                }
                sg.setFinalGrade(finalGrade);
            }
        }

        return new SortResponse(studentGrades, true, bonus);
    }

    /**
     * ★ DSA Core: LSD Radix Sort
     * Sorts StudentGrade list by their originalGrade in ascending order.
     */
    private void radixSort(List<StudentGrade> list) {
        if (list.isEmpty()) return;

        // Convert grades to integers for radix sort (multiply by 100 to preserve 2 decimals)
        int[] grades = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            BigDecimal grade = list.get(i).getOriginalGrade();
            grades[i] = grade != null
                    ? grade.multiply(BigDecimal.valueOf(100)).intValue()
                    : 0;
        }

        // Step 2: Find max grade
        int max = Arrays.stream(grades).max().orElse(0);

        // Step 3: Set exp = 1
        int exp = 1;

        // We need an auxiliary array for the sorted indices
        Integer[] indices = new Integer[list.size()];
        for (int i = 0; i < indices.length; i++) indices[i] = i;

        // Step 4: While max/exp > 0
        while (max / exp > 0) {
            // Step 4a: Create 10 buckets (0-9)
            @SuppressWarnings("unchecked")
            List<Integer>[] buckets = new ArrayList[10];
            for (int i = 0; i < 10; i++) {
                buckets[i] = new ArrayList<>();
            }

            // Step 4b: Distribute students: digit = (grade / exp) mod 10
            for (int idx : indices) {
                int digit = (grades[idx] / exp) % 10;
                buckets[digit].add(idx);
            }

            // Step 4c: Collect buckets (from 0-9) — ascending order
            int pos = 0;
            for (int i = 0; i < 10; i++) {
                for (int idx : buckets[i]) {
                    indices[pos++] = idx;
                }
            }

            // Step 4d: exp = exp * 10
            exp *= 10;
        }

        // Rebuild the list in sorted order
        List<StudentGrade> sorted = new ArrayList<>();
        for (int idx : indices) {
            sorted.add(list.get(idx));
        }
        list.clear();
        list.addAll(sorted);
    }

    private BigDecimal computeAverage(List<Grade> grades, List<Activity> activities) {
        if (grades.isEmpty() || activities.isEmpty()) return BigDecimal.ZERO;

        Map<Long, BigDecimal> maxScoreMap = new HashMap<>();
        for (Activity a : activities) {
            maxScoreMap.put(a.getId(), a.getMaxScore());
        }

        BigDecimal totalPercent = BigDecimal.ZERO;
        int count = 0;

        for (Grade g : grades) {
            if (g.getScore() != null) {
                BigDecimal maxScore = maxScoreMap.get(g.getActivity().getId());
                if (maxScore != null && maxScore.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percent = g.getScore()
                            .divide(maxScore, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    totalPercent = totalPercent.add(percent);
                    count++;
                }
            }
        }

        return count > 0
                ? totalPercent.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }
}
