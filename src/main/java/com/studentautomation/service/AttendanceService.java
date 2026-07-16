package com.studentautomation.service;

import com.studentautomation.dto.request.AttendanceRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRequestDTO;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.BulkAttendanceResponseDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for attendance-related operations.
 *
 * Purpose:
 * This interface defines the business operations for marking,
 * viewing, and managing student attendance.
 *
 * @author Yashvanth
 */
public interface AttendanceService {

    /**
     * Marks attendance for a student by a teacher.
     *
     * Purpose:
     * This method allows a teacher to mark attendance for a specific student,
     * subject, date, and period.
     *
     * @param request attendance request data
     * @param teacherEmail email of the logged-in teacher
     * @return saved attendance response
     */
    AttendanceResponseDTO markAttendance(AttendanceRequestDTO request, String teacherEmail);

    /**
     * Gets attendance records marked by the logged-in teacher on a specific date.
     *
     * Purpose:
     * This method allows a teacher to view attendance records marked by them
     * for a particular date.
     *
     * @param attendanceDate date of attendance
     * @param teacherEmail email of the logged-in teacher
     * @return list of attendance records
     */
    List<AttendanceResponseDTO> getAttendanceByDateForTeacher(LocalDate attendanceDate, String teacherEmail);

    /**
     * Gets attendance records of the logged-in student.
     *
     * Purpose:
     * This method allows a student to view only their own attendance records.
     *
     * @param studentEmail email of the logged-in student
     * @return list of student's attendance records
     */
    List<AttendanceResponseDTO> getMyAttendance(String studentEmail);

    /**
     * Marks attendance for multiple students at once.
     *
     * Purpose:
     * Teacher can mark attendance for an entire class/section
     * using one API call.
     *
     * @param request bulk attendance request data
     * @param teacherEmail logged-in teacher email from JWT
     * @return bulk attendance result
     */
    BulkAttendanceResponseDTO markBulkAttendance(
            BulkAttendanceRequestDTO request,
            String teacherEmail
    );


}