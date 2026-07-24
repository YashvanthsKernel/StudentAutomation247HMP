package com.studentautomation.service.impl;

import com.studentautomation.dto.request.BulkMarkRequestDTO;
import com.studentautomation.dto.request.MarkRequestDTO;
import com.studentautomation.dto.response.MarkReportResponseDTO;
import com.studentautomation.dto.response.MarkResponseDTO;
import com.studentautomation.dto.response.StudentMarkSummaryResponseDTO;
import com.studentautomation.entity.*;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.*;
import com.studentautomation.service.MarkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkServiceImpl implements MarkService {

    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicClassRepository academicClassRepository;

    public MarkServiceImpl(MarkRepository markRepository,
                           StudentRepository studentRepository,
                           SubjectRepository subjectRepository,
                           ExamRepository examRepository,
                           TeacherRepository teacherRepository,
                           AcademicClassRepository academicClassRepository) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.examRepository = examRepository;
        this.teacherRepository = teacherRepository;
        this.academicClassRepository = academicClassRepository;
    }

    @Override
    @Transactional
    public MarkResponseDTO recordMark(MarkRequestDTO request, String teacherEmail) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Exam exam = examRepository.findById(request.examId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        Double maxMarks = request.maxMarks() != null && request.maxMarks() > 0 ? request.maxMarks() : 100.0;
        if (request.marksObtained() > maxMarks) {
            throw new InvalidRequestException("Marks obtained cannot exceed maximum marks (" + maxMarks + ")");
        }

        Optional<Mark> existingOpt = markRepository.findByStudent_IdAndSubject_IdAndExam_Id(
                student.getId(), subject.getId(), exam.getId()
        );

        Mark mark;
        if (existingOpt.isPresent()) {
            mark = existingOpt.get();
        } else {
            mark = new Mark();
            mark.setStudent(student);
            mark.setSubject(subject);
            mark.setExam(exam);
        }

        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail).orElse(null);
        if (teacher != null) {
            mark.setEnteredBy(teacher);
        }

        mark.setMarksObtained(request.marksObtained());
        mark.setMaxMarks(maxMarks);
        mark.setSection(request.section() != null ? request.section() : student.getSection());
        mark.setAcademicYear(request.academicYear() != null ? request.academicYear() : student.getAcademicYear());

        Mark saved = markRepository.save(mark);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public List<MarkResponseDTO> recordBulkMarks(BulkMarkRequestDTO request, String teacherEmail) {
        List<MarkResponseDTO> responseList = new ArrayList<>();
        Double maxMarks = request.maxMarks() != null && request.maxMarks() > 0 ? request.maxMarks() : 100.0;

        for (BulkMarkRequestDTO.StudentMarkEntry entry : request.marks()) {
            MarkRequestDTO singleReq = new MarkRequestDTO(
                    entry.studentId(),
                    request.subjectId(),
                    request.examId(),
                    entry.marksObtained(),
                    maxMarks,
                    request.section(),
                    request.academicYear()
            );
            responseList.add(recordMark(singleReq, teacherEmail));
        }
        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getMarksForTeacher(Long examId, Long subjectId, String section, String teacherEmail) {
        List<Mark> marks;
        if (section != null && !section.isBlank()) {
            marks = markRepository.findByExam_IdAndSubject_IdAndSection(examId, subjectId, section);
        } else {
            marks = markRepository.findByExam_IdAndSubject_Id(examId, subjectId);
        }
        return marks.stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MarkResponseDTO getMarkById(Long markId) {
        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new ResourceNotFoundException("Mark record not found"));
        return mapToDTO(mark);
    }

    @Override
    @Transactional
    public MarkResponseDTO updateMark(Long markId, MarkRequestDTO request, String teacherEmail) {
        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new ResourceNotFoundException("Mark record not found"));

        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail).orElse(null);
        if (teacher != null && mark.getEnteredBy() != null && !mark.getEnteredBy().getId().equals(teacher.getId())) {
            throw new InvalidRequestException("You are not authorized to modify marks entered by another teacher");
        }

        Double maxMarks = request.maxMarks() != null && request.maxMarks() > 0 ? request.maxMarks() : mark.getMaxMarks();
        if (request.marksObtained() > maxMarks) {
            throw new InvalidRequestException("Marks obtained cannot exceed maximum marks (" + maxMarks + ")");
        }

        mark.setMarksObtained(request.marksObtained());
        mark.setMaxMarks(maxMarks);
        return mapToDTO(markRepository.save(mark));
    }

    @Override
    @Transactional
    public void deleteMark(Long markId, String teacherEmail) {
        Mark mark = markRepository.findById(markId)
                .orElseThrow(() -> new ResourceNotFoundException("Mark record not found"));

        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail).orElse(null);
        if (teacher != null && mark.getEnteredBy() != null && !mark.getEnteredBy().getId().equals(teacher.getId())) {
            throw new InvalidRequestException("You are not authorized to delete marks entered by another teacher");
        }

        markRepository.delete(mark);
    }

    @Override
    @Transactional
    public void publishMarksForExamAndSubject(Long examId, Long subjectId, String teacherEmail) {
        List<Mark> marks = markRepository.findByExam_IdAndSubject_Id(examId, subjectId);
        for (Mark mark : marks) {
            mark.setIsPublished(true);
        }
        markRepository.saveAll(marks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getMyMarks(String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return markRepository.findByStudent_Id(student.getId())
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsPublished()))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getMyMarksByExam(Long examId, String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return markRepository.findByStudent_IdAndExam_Id(student.getId(), examId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsPublished()))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getMyMarksBySubject(Long subjectId, String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return markRepository.findByStudent_IdAndSubject_Id(student.getId(), subjectId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsPublished()))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentMarkSummaryResponseDTO getMyMarksSummary(String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<Mark> marks = markRepository.findByStudent_Id(student.getId())
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsPublished()))
                .toList();

        double totalObtained = 0.0;
        double totalMax = 0.0;
        int passed = 0;
        int failed = 0;

        List<MarkResponseDTO> dtos = new ArrayList<>();
        for (Mark m : marks) {
            totalObtained += m.getMarksObtained();
            totalMax += m.getMaxMarks();
            if (Boolean.TRUE.equals(m.getIsPass())) passed++;
            else failed++;
            dtos.add(mapToDTO(m));
        }

        double overallPct = totalMax > 0 ? (totalObtained / totalMax) * 100.0 : 0.0;
        String overallGrade;
        if (overallPct >= 90) overallGrade = "S";
        else if (overallPct >= 80) overallGrade = "A+";
        else if (overallPct >= 70) overallGrade = "A";
        else if (overallPct >= 60) overallGrade = "B";
        else if (overallPct >= 50) overallGrade = "C";
        else if (overallPct >= 40) overallGrade = "D";
        else overallGrade = "F";

        return new StudentMarkSummaryResponseDTO(
                student.getId(),
                student.getRegNo(),
                student.getName(),
                totalObtained,
                totalMax,
                Math.round(overallPct * 100.0) / 100.0,
                overallGrade,
                marks.size(),
                passed,
                failed,
                dtos
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getMarksByStudentId(Long studentId) {
        return markRepository.findByStudent_Id(studentId).stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getMarksByClassId(Long classId) {
        AcademicClass ac = academicClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        List<Student> students = studentRepository.findByDepartmentAndSemester(ac.getDepartmentCode(), ac.getSemester())
                .stream()
                .filter(s -> s.getSection() != null && s.getSection().equalsIgnoreCase(ac.getSection()))
                .toList();

        List<MarkResponseDTO> allMarks = new ArrayList<>();
        for (Student s : students) {
            allMarks.addAll(markRepository.findByStudent_Id(s.getId()).stream().map(this::mapToDTO).toList());
        }
        return allMarks;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getMarksBySubjectId(Long subjectId) {
        return markRepository.findBySubject_Id(subjectId).stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getFailures() {
        return markRepository.findByIsPassFalse().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarkResponseDTO> getTopPerformers() {
        return markRepository.findTop10ByOrderByMarksObtainedDesc().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MarkReportResponseDTO getMarksReport() {
        List<Mark> allMarks = markRepository.findAll();
        if (allMarks.isEmpty()) {
            return new MarkReportResponseDTO(0, 0.0, 0.0, 0.0, 0, 0, 0.0);
        }

        double totalPct = 0.0;
        double highest = Double.MIN_VALUE;
        double lowest = Double.MAX_VALUE;
        int passed = 0;
        int failed = 0;

        for (Mark m : allMarks) {
            totalPct += m.getPercentage();
            if (m.getMarksObtained() > highest) highest = m.getMarksObtained();
            if (m.getMarksObtained() < lowest) lowest = m.getMarksObtained();
            if (Boolean.TRUE.equals(m.getIsPass())) passed++;
            else failed++;
        }

        double avgPct = totalPct / allMarks.size();
        double passPct = (double) passed / allMarks.size() * 100.0;

        return new MarkReportResponseDTO(
                allMarks.size(),
                Math.round(avgPct * 100.0) / 100.0,
                highest,
                lowest,
                passed,
                failed,
                Math.round(passPct * 100.0) / 100.0
        );
    }

    private MarkResponseDTO mapToDTO(Mark m) {
        return new MarkResponseDTO(
                m.getId(),
                m.getStudent().getId(),
                m.getStudent().getRegNo(),
                m.getStudent().getName(),
                m.getSubject().getId(),
                m.getSubject().getSubjectCode(),
                m.getSubject().getSubjectName(),
                m.getExam().getId(),
                m.getExam().getExamName(),
                m.getExam().getExamCode(),
                m.getMarksObtained(),
                m.getMaxMarks(),
                m.getPercentage(),
                m.getGrade(),
                m.getIsPass(),
                m.getSection(),
                m.getAcademicYear(),
                m.getIsPublished(),
                m.getCreatedAt()
        );
    }
}
