package com.studentautomation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing the assignment of a subject to a student.
 *
 * Purpose:
 * Connects a student with an academic subject for a specific
 * academic year.
 *
 * This assignment will later be used for subject-wise attendance,
 * marks, timetable, and student subject access.
 *
 * Example:
 * Student 2 studies CS301 during academic year 2026-2027.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "student_subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_subject_year",
                        columnNames = {
                                "student_id",
                                "subject_id",
                                "academic_year"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class StudentSubject {

    /**
     * Primary key of the student-subject assignment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Student assigned to the subject.
     *
     * Many subject assignments can belong to one student.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_student_subject_student"
            )
    )
    private Student student;

    /**
     * Subject assigned to the student.
     *
     * Many student assignments can refer to one subject.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "subject_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_student_subject_subject"
            )
    )
    private Subject subject;

    /**
     * Academic year for which the student studies the subject.
     *
     * Example:
     * 2026-2027
     */
    @Column(
            name = "academic_year",
            nullable = false,
            length = 20
    )
    private String academicYear;

    /**
     * Indicates whether the subject assignment is currently active.
     *
     * An inactive assignment remains in the database
     * for maintaining academic history.
     */
    @Column(
            name = "active",
            nullable = false
    )
    private Boolean active = true;

    /**
     * Date and time when the assignment was created.
     */
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    /**
     * Date and time when the assignment was last updated.
     */
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    /**
     * Constructor used by Lombok's builder.
     *
     * Created and updated timestamps are not included because
     * JPA lifecycle methods manage them automatically.
     *
     * @param id assignment ID
     * @param student assigned student
     * @param subject assigned subject
     * @param academicYear academic year
     * @param active assignment status
     */
    @Builder
    public StudentSubject(
            Long id,
            Student student,
            Subject subject,
            String academicYear,
            Boolean active
    ) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.academicYear = academicYear;
        this.active = active;
    }

    /**
     * Sets timestamps and default values before inserting
     * the assignment into the database.
     */
    @PrePersist
    protected void onCreate() {

        LocalDateTime currentTime = LocalDateTime.now();

        this.createdAt = currentTime;
        this.updatedAt = currentTime;

        if (this.active == null) {
            this.active = true;
        }
    }

    /**
     * Updates the modified timestamp before updating
     * the assignment in the database.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}