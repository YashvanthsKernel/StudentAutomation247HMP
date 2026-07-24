package com.studentautomation.service.impl;

import com.studentautomation.dto.request.ExamScheduleRequestDTO;
import com.studentautomation.dto.response.ExamScheduleResponseDTO;
import com.studentautomation.entity.*;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.*;
import com.studentautomation.service.ExamScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamScheduleServiceImpl implements ExamScheduleService {

    private final ExamScheduleRepository examScheduleRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public ExamScheduleServiceImpl(
            ExamScheduleRepository examScheduleRepository,
            ExamRepository examRepository,
            SubjectRepository subjectRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository
    ) {
        this.examScheduleRepository = examScheduleRepository;
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    @Transactional
    public ExamScheduleResponseDTO createExamSchedule(ExamScheduleRequestDTO request) {
        Exam exam = examRepository.findById(request.examId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        ExamSchedule es = new ExamSchedule();
        es.setExam(exam);
        es.setSubject(subject);
        es.setExamDate(request.examDate());
        es.setStartTime(request.startTime());
        es.setEndTime(request.endTime());
        es.setRoomNumber(request.roomNumber());
        es.setAcademicYear(request.academicYear() != null ? request.academicYear() : exam.getAcademicYear());

        return mapToDTO(examScheduleRepository.save(es));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamScheduleResponseDTO> getAllExamSchedules() {
        return examScheduleRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional
    public ExamScheduleResponseDTO updateExamSchedule(Long id, ExamScheduleRequestDTO request) {
        ExamSchedule es = examScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found"));

        Exam exam = examRepository.findById(request.examId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        es.setExam(exam);
        es.setSubject(subject);
        es.setExamDate(request.examDate());
        es.setStartTime(request.startTime());
        es.setEndTime(request.endTime());
        es.setRoomNumber(request.roomNumber());

        return mapToDTO(examScheduleRepository.save(es));
    }

    @Override
    @Transactional
    public void deleteExamSchedule(Long id) {
        ExamSchedule es = examScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found"));
        examScheduleRepository.delete(es);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamScheduleResponseDTO> getMyStudentExamSchedules(String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return examScheduleRepository.findAll()
                .stream()
                .filter(es -> es.getSubject().getDepartment().equalsIgnoreCase(student.getDepartment())
                           && es.getSubject().getSemester().equals(student.getSemester()))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamScheduleResponseDTO> getMyTeacherExamSchedules(String teacherEmail) {
        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        return examScheduleRepository.findAll()
                .stream()
                .filter(es -> es.getSubject().getDepartment().equalsIgnoreCase(teacher.getDepartment()))
                .map(this::mapToDTO)
                .toList();
    }

    private ExamScheduleResponseDTO mapToDTO(ExamSchedule es) {
        return new ExamScheduleResponseDTO(
                es.getId(),
                es.getExam().getId(),
                es.getExam().getExamName(),
                es.getExam().getExamCode(),
                es.getSubject().getId(),
                es.getSubject().getSubjectCode(),
                es.getSubject().getSubjectName(),
                es.getExamDate(),
                es.getStartTime(),
                es.getEndTime(),
                es.getRoomNumber(),
                es.getAcademicYear(),
                es.getCreatedAt()
        );
    }
}
