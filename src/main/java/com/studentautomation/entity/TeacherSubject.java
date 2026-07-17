package com.studentautomation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing the assignment of a subject to a teacher.
 *
 * Purpose:
 * Connects a teacher with a subject for a particular section
 * and academic year.
 *
 * Example:
 * Teacher 5 teaches CS301 to Section A during 2026-2027.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "teacher_subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_teacher_subject_section_year",
                        columnNames = {
                                "teacher_id",
                                "subject_id",
                                "section",
                                "academic_year"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class TeacherSubject {

    /**
     * Primary key of the teacher-subject assignment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Teacher assigned to teach the subject.
     *
     * Many assignments can belong to one teacher.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "teacher_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_teacher_subject_teacher"
            )
    )
    private Teacher teacher;

    /**
     * Subject assigned to the teacher.
     *
     * Many teacher assignments can refer to one subject.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "subject_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_teacher_subject_subject"
            )
    )
    private Subject subject;

    /**
     * Class section for which the teacher handles the subject.
     *
     * Example:
     * A, B, C
     */
    @Column(
            name = "section",
            nullable = false,
            length = 20
    )
    private String section;

    /**
     * Academic year of the assignment.
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
     * Indicates whether the assignment is currently active.
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
     * @param id assignment ID
     * @param teacher assigned teacher
     * @param subject assigned subject
     * @param section class section
     * @param academicYear academic year
     * @param active assignment status
     */
    @Builder
    public TeacherSubject(
            Long id,
            Teacher teacher,
            Subject subject,
            String section,
            String academicYear,
            Boolean active
    ) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.section = section;
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
     * the assignment.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}