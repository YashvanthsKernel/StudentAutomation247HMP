package com.studentautomation.dto.response;

import com.studentautomation.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for attendance details.
 *
 * Purpose:
 * This record sends attendance information back to the frontend
 * after an attendance record is created, updated, or fetched.
 *
 * @param id attendance record ID
 * @param studentId ID of the student
 * @param studentName name of the student
 * @param teacherId ID of the teacher who marked attendance
 * @param teacherName name of the teacher who marked attendance
 * @param subjectId ID of the assigned subject
 * @param subjectCode unique code of the subject
 * @param subjectName name of the subject
 * @param attendanceDate date for which attendance was marked
 * @param periodNumber class period number
 * @param status attendance status
 * @param remarks optional remarks added by the teacher
 * @param createdAt date and time when the record was created
 * @param updatedAt date and time when the record was last updated
 *
 * @author Yashvanth
 */
public record AttendanceResponseDTO(

        /**
         * Unique ID of the attendance record.
         */
        Long id,

        /**
         * ID of the student whose attendance was marked.
         */
        Long studentId,

        /**
         * Name of the student whose attendance was marked.
         */
        String studentName,

        /**
         * ID of the teacher who marked the attendance.
         */
        Long teacherId,

        /**
         * Name of the teacher who marked the attendance.
         */
        String teacherName,

        /**
         * ID of the subject for which attendance was marked.
         */
        Long subjectId,

        /**
         * Unique code of the subject.
         *
         * Example:
         * CS101
         */
        String subjectCode,

        /**
         * Name of the subject.
         *
         * Example:
         * Core Java
         */
        String subjectName,

        /**
         * Date for which attendance was marked.
         */
        LocalDate attendanceDate,

        /**
         * Period number during which the class occurred.
         */
        Integer periodNumber,

        /**
         * Attendance status of the student.
         */
        AttendanceStatus status,

        /**
         * Optional remarks added by the teacher.
         */
        String remarks,

        /**
         * Date and time when the attendance record was created.
         */
        LocalDateTime createdAt,

        /**
         * Date and time when the attendance record was last updated.
         */
        LocalDateTime updatedAt
) {
}