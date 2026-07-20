package com.studentautomation.entity;

import com.studentautomation.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing attendance records of students.
 *
 * Purpose:
 * This class stores the attendance details of a student for a specific
 * subject, date, and period. Each attendance record is marked by a teacher.
 *
 * Important Rule:
 * A student must not have duplicate attendance for the same
 * subject, attendance date, and period.
 *
 * @author Yashvanth
 */
@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_attendance_date_subject_period",
                        columnNames = {
                                "student_id",
                                "attendance_date",
                                "subject_id",
                                "period_number"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Attendance {

    /**
     * Unique ID of the attendance record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Student whose attendance is being marked.
     *
     * Many attendance records can belong to one student.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Teacher who marked the attendance.
     *
     * The authenticated teacher will be fetched from the database
     * and stored in this relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marked_by_teacher_id", nullable = false)
    private Teacher markedBy;

    /**
     * Subject for which attendance is being marked.
     *
     * The subject will be fetched from the subjects table using
     * the subjectId received in the attendance request.
     *
     * Many attendance records can belong to one subject.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /**
     * Date for which attendance is marked.
     */
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    /**
     * Period number of the class.
     *
     * Example:
     * 1 means first period and 2 means second period.
     */
    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;

    /**
     * Attendance status of the student.
     *
     * Example:
     * PRESENT, ABSENT, LATE, EXCUSED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    /**
     * Optional remarks added by the teacher.
     */
    @Column(length = 255)
    private String remarks;

    /**
     * Time when this attendance record was created.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Time when this attendance record was last updated.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Automatically sets the creation and update timestamps
     * before inserting the attendance record.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    /**
     * Automatically updates the modification timestamp
     * before updating the attendance record.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}