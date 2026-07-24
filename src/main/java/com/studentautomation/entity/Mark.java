package com.studentautomation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class for storing Student Marks.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "marks",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mark_student_subject_exam", columnNames = {"student_id", "subject_id", "exam_id"})
        }
)
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entered_by_teacher_id")
    private Teacher enteredBy;

    @Column(name = "marks_obtained", nullable = false)
    private Double marksObtained;

    @Column(name = "max_marks", nullable = false)
    private Double maxMarks = 100.0;

    @Column(name = "percentage")
    private Double percentage;

    @Column(length = 10)
    private String grade; // S, A+, A, B, C, D, F

    @Column(name = "is_pass")
    private Boolean isPass;

    @Column(name = "academic_year", length = 30)
    private String academicYear;

    @Column(length = 20)
    private String section;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Mark() {
    }

    @PrePersist
    public void beforeSave() {
        this.createdAt = LocalDateTime.now();
        calculateGradeAndPercentage();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateGradeAndPercentage();
    }

    public void calculateGradeAndPercentage() {
        if (marksObtained != null && maxMarks != null && maxMarks > 0) {
            this.percentage = Math.round((marksObtained / maxMarks) * 100.0 * 100.0) / 100.0;
            this.isPass = this.percentage >= 40.0;
            if (percentage >= 90) this.grade = "S";
            else if (percentage >= 80) this.grade = "A+";
            else if (percentage >= 70) this.grade = "A";
            else if (percentage >= 60) this.grade = "B";
            else if (percentage >= 50) this.grade = "C";
            else if (percentage >= 40) this.grade = "D";
            else this.grade = "F";
        }
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Teacher getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(Teacher enteredBy) {
        this.enteredBy = enteredBy;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Boolean getIsPass() {
        return isPass;
    }

    public void setIsPass(Boolean isPass) {
        this.isPass = isPass;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
