package com.studentautomation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO used to return teacher-subject assignment details.
 *
 * Purpose:
 * Returns useful teacher and subject information without exposing
 * the complete TeacherSubject, Teacher, or Subject entities.
 *
 * @author Yashvanth
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSubjectResponseDTO {

    /**
     * Unique ID of the teacher-subject assignment.
     */
    private Long id;

    /**
     * Database ID of the assigned teacher.
     */
    private Long teacherId;

    /**
     * Employee ID of the assigned teacher.
     */
    private String teacherEmployeeId;

    /**
     * Name of the assigned teacher.
     */
    private String teacherName;

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
     * Department obtained from the Subject entity.
     */
    private String department;

    /**
     * Semester obtained from the Subject entity.
     */
    private Integer semester;

    /**
     * Section handled by the teacher.
     */
    private String section;

    /**
     * Academic year of the assignment.
     */
    private String academicYear;

    /**
     * Indicates whether the assignment is active.
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