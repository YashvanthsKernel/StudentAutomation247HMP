package com.studentautomation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing an academic subject.
 *
 * Purpose:
 * Stores subject information such as subject code, subject name,
 * department, semester, credits, and active status.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_subject_code",
                        columnNames = "subject_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Subject {

    /**
     * Primary key of the subject.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique code used to identify the subject.
     *
     * Example:
     * CS301, MA201
     */
    @Column(
            name = "subject_code",
            nullable = false,
            unique = true,
            length = 30
    )
    private String subjectCode;

    /**
     * Display name of the subject.
     */
    @Column(
            name = "subject_name",
            nullable = false,
            length = 150
    )
    private String subjectName;

    /**
     * Department to which the subject belongs.
     *
     * Example:
     * CSE, ECE, MECH
     */
    @Column(
            name = "department",
            nullable = false,
            length = 100
    )
    private String department;

    /**
     * Semester in which the subject is taught.
     */
    @Column(
            name = "semester",
            nullable = false
    )
    private Integer semester;

    /**
     * Academic credits assigned to the subject.
     */
    @Column(
            name = "credits",
            nullable = false
    )
    private Integer credits;

    /**
     * Determines whether the subject is currently active.
     */
    @Column(
            name = "active",
            nullable = false
    )
    private Boolean active = true;

    /**
     * Date and time when the subject was created.
     */
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    /**
     * Date and time when the subject was last updated.
     */
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    /**
     * Constructor used by Lombok's builder.
     *
     * We are not allowing createdAt and updatedAt through the builder
     * because the entity lifecycle methods manage them automatically.
     *
     * @param id subject ID
     * @param subjectCode unique subject code
     * @param subjectName subject name
     * @param department department name
     * @param semester semester number
     * @param credits subject credits
     * @param active active status
     */
    @Builder
    public Subject(
            Long id,
            String subjectCode,
            String subjectName,
            String department,
            Integer semester,
            Integer credits,
            Boolean active
    ) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.department = department;
        this.semester = semester;
        this.credits = credits;
        this.active = active;
    }

    /**
     * Sets timestamps and default values before inserting
     * the subject into the database.
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
     * the subject in the database.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}