package com.studentautomation.service;

import com.studentautomation.dto.request.BulkMarkRequestDTO;
import com.studentautomation.dto.request.MarkRequestDTO;
import com.studentautomation.dto.response.MarkReportResponseDTO;
import com.studentautomation.dto.response.MarkResponseDTO;
import com.studentautomation.dto.response.StudentMarkSummaryResponseDTO;

import java.util.List;

public interface MarkService {
    // Teacher operations
    MarkResponseDTO recordMark(MarkRequestDTO request, String teacherEmail);
    List<MarkResponseDTO> recordBulkMarks(BulkMarkRequestDTO request, String teacherEmail);
    List<MarkResponseDTO> getMarksForTeacher(Long examId, Long subjectId, String section, String teacherEmail);
    MarkResponseDTO getMarkById(Long markId);
    MarkResponseDTO updateMark(Long markId, MarkRequestDTO request, String teacherEmail);
    void deleteMark(Long markId, String teacherEmail);
    void publishMarksForExamAndSubject(Long examId, Long subjectId, String teacherEmail);

    // Student operations
    List<MarkResponseDTO> getMyMarks(String studentEmail);
    List<MarkResponseDTO> getMyMarksByExam(Long examId, String studentEmail);
    List<MarkResponseDTO> getMyMarksBySubject(Long subjectId, String studentEmail);
    StudentMarkSummaryResponseDTO getMyMarksSummary(String studentEmail);

    // Admin operations
    List<MarkResponseDTO> getMarksByStudentId(Long studentId);
    List<MarkResponseDTO> getMarksByClassId(Long classId);
    List<MarkResponseDTO> getMarksBySubjectId(Long subjectId);
    List<MarkResponseDTO> getFailures();
    List<MarkResponseDTO> getTopPerformers();
    MarkReportResponseDTO getMarksReport();
}
