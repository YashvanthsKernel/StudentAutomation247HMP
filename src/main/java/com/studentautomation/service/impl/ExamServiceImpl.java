package com.studentautomation.service.impl;

import com.studentautomation.dto.request.ExamRequestDTO;
import com.studentautomation.dto.response.ExamResponseDTO;
import com.studentautomation.entity.Exam;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.ExamRepository;
import com.studentautomation.service.ExamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    @Transactional
    public ExamResponseDTO createExam(ExamRequestDTO request) {
        String codeUpper = request.examCode().trim().toUpperCase();
        if (examRepository.existsByExamCodeIgnoreCase(codeUpper)) {
            throw new DuplicateResourceException("Exam code already exists");
        }

        Exam exam = new Exam();
        exam.setExamName(request.examName().trim());
        exam.setExamCode(codeUpper);
        exam.setAcademicYear(request.academicYear().trim());
        exam.setStartDate(request.startDate());
        exam.setEndDate(request.endDate());
        exam.setStatus("DRAFT");
        exam.setActive(true);

        return mapToDTO(examRepository.save(exam));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDTO> getAllExams() {
        return examRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO getExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        return mapToDTO(exam);
    }

    @Override
    @Transactional
    public ExamResponseDTO updateExam(Long examId, ExamRequestDTO request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        String codeUpper = request.examCode().trim().toUpperCase();
        if (!exam.getExamCode().equalsIgnoreCase(codeUpper) && examRepository.existsByExamCodeIgnoreCase(codeUpper)) {
            throw new DuplicateResourceException("Exam code already exists");
        }

        exam.setExamName(request.examName().trim());
        exam.setExamCode(codeUpper);
        exam.setAcademicYear(request.academicYear().trim());
        exam.setStartDate(request.startDate());
        exam.setEndDate(request.endDate());

        return mapToDTO(examRepository.save(exam));
    }

    @Override
    @Transactional
    public ExamResponseDTO publishExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        exam.setStatus("PUBLISHED");
        return mapToDTO(examRepository.save(exam));
    }

    @Override
    @Transactional
    public ExamResponseDTO closeExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        exam.setStatus("CLOSED");
        return mapToDTO(examRepository.save(exam));
    }

    private ExamResponseDTO mapToDTO(Exam exam) {
        return new ExamResponseDTO(
                exam.getId(),
                exam.getExamName(),
                exam.getExamCode(),
                exam.getAcademicYear(),
                exam.getStartDate(),
                exam.getEndDate(),
                exam.getStatus(),
                exam.getActive(),
                exam.getCreatedAt()
        );
    }
}
