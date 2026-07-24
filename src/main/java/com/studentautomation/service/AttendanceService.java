package com.studentautomation.service;

import com.studentautomation.dto.request.AttendanceRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRequestDTO;
import com.studentautomation.dto.response.AttendanceReportDTO;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.BulkAttendanceResponseDTO;
import com.studentautomation.dto.response.StudentAttendanceSummaryDTO;
import com.studentautomation.dto.response.StudentResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponseDTO markAttendance(AttendanceRequestDTO request, String teacherEmail);

    List<AttendanceResponseDTO> getAttendanceByDateForTeacher(LocalDate attendanceDate, String teacherEmail);

    List<AttendanceResponseDTO> getAttendanceWithFilters(LocalDate date, Long subjectId, String section, Integer periodNumber, String teacherEmail);

    List<StudentResponseDTO> getAttendanceRoster(Long subjectId, String section, String academicYear, String teacherEmail);

    AttendanceResponseDTO getAttendanceById(Long attendanceId);

    AttendanceResponseDTO updateAttendance(Long attendanceId, AttendanceRequestDTO request, String teacherEmail);

    AttendanceResponseDTO updateAttendanceStatus(Long attendanceId, String status, String teacherEmail);

    void deleteAttendance(Long attendanceId, String teacherEmail);

    List<AttendanceResponseDTO> getMyAttendance(String studentEmail);

    List<AttendanceResponseDTO> getMyAttendanceFiltered(Long subjectId, LocalDate fromDate, LocalDate toDate, String studentEmail);

    StudentAttendanceSummaryDTO getMyAttendanceSummary(String studentEmail);

    StudentAttendanceSummaryDTO getMySubjectAttendanceSummary(Long subjectId, String studentEmail);

    BulkAttendanceResponseDTO markBulkAttendance(BulkAttendanceRequestDTO request, String teacherEmail);

    // Admin endpoints
    List<AttendanceResponseDTO> getAllAttendance();

    List<AttendanceResponseDTO> getAttendanceByStudentId(Long studentId);

    List<AttendanceResponseDTO> getAttendanceByClassId(Long classId);

    List<AttendanceResponseDTO> getAttendanceBySubjectId(Long subjectId);

    List<StudentAttendanceSummaryDTO> getAttendanceShortage();

    AttendanceReportDTO getAttendanceReport();
}