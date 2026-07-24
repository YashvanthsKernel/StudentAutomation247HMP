package com.studentautomation.service;

import com.studentautomation.dto.request.ExamRequestDTO;
import com.studentautomation.dto.response.ExamResponseDTO;
import java.util.List;

public interface ExamService {
    ExamResponseDTO createExam(ExamRequestDTO request);
    List<ExamResponseDTO> getAllExams();
    ExamResponseDTO getExamById(Long examId);
    ExamResponseDTO updateExam(Long examId, ExamRequestDTO request);
    ExamResponseDTO publishExam(Long examId);
    ExamResponseDTO closeExam(Long examId);
}
