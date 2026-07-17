package com.studentautomation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO used to return student-subject assignment details.
 *
 * Purpose:
 * Returns useful student and subject information without exposing
 * the complete StudentSubject, Student, or Subject entities.
 *
 * @author Yashvanth
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectResponseDTO {

    /**
     * Unique database ID of the student-subject assignment.
     */
    private Long id;

    /**
     * Database ID of the assigned student.
     */
    private Long studentId;

    /**
     * Register number of the assigned student.
     */
    private String studentRegNo;

    /**
     * Name of the assigned student.
     */
    private String studentName;

    /**
     * Department of the assigned student.
     */
    private String studentDepartment;

    /**
     * Semester in which the student is currently studying.
     */
    private Integer studentSemester;

    /**
     * Section of the assigned student.
     */
    private String studentSection;

    /**
     * Database ID of the assigned subject.
     */
    private Long subjectId;

    /**
     * Unique code of the assigned subject.
     */
    private String subjectCode;

    /**
     * Name of the assigned subject.
     */
    private String subjectName;

    /**
     * Department to which the subject belongs.
     */
    private String subjectDepartment;

    /**
     * Semester in which the subject is taught.
     */
    private Integer subjectSemester;

    /**
     * Academic year of the assignment.
     */
    private String academicYear;

    /**
     * Indicates whether the assignment is currently active.
     */
    private Boolean active;

    /**
     * Date and time when the assignment was created.
     */
    private LocalDateTime createdAt;

    /**
     * Date and time when the assignment was last updated.
     */
    private LocalDateTime updatedAt;
}