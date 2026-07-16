package com.studentautomation.dto.response;

import com.studentautomation.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for attendance details.
 *
 * Purpose:
 * This class sends attendance record details back to frontend/Postman
 * after attendance is created, updated, or fetched.
 *
 * @param id attendance record ID
 * @param studentId student ID
 * @param studentName student name
 * @param teacherId teacher ID who marked attendance
 * @param teacherName teacher name who marked attendance
 * @param attendanceDate attendance date
 * @param subjectName subject name
 * @param periodNumber class period number
 * @param status attendance status
 * @param remarks optional remarks
 * @param createdAt record created time
 * @param updatedAt record updated time
 *
 * @author Yashvanth
 */
public record AttendanceResponseDTO(

        Long id,
        Long studentId,
        String studentName,
        Long teacherId,
        String teacherName,
        LocalDate attendanceDate,
        String subjectName,
        Integer periodNumber,
        AttendanceStatus status,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}