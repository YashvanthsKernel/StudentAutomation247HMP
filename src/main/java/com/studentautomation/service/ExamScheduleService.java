package com.studentautomation.service;

import com.studentautomation.dto.request.ExamScheduleRequestDTO;
import com.studentautomation.dto.response.ExamScheduleResponseDTO;

import java.util.List;

public interface ExamScheduleService {
    ExamScheduleResponseDTO createExamSchedule(ExamScheduleRequestDTO request);
    List<ExamScheduleResponseDTO> getAllExamSchedules();
    ExamScheduleResponseDTO updateExamSchedule(Long id, ExamScheduleRequestDTO request);
    void deleteExamSchedule(Long id);

    List<ExamScheduleResponseDTO> getMyStudentExamSchedules(String studentEmail);
    List<ExamScheduleResponseDTO> getMyTeacherExamSchedules(String teacherEmail);
}
