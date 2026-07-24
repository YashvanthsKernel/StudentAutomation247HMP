package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AssignClassSubjectRequestDTO;
import com.studentautomation.dto.request.BulkStudentSubjectRequestDTO;
import com.studentautomation.dto.request.StudentSubjectRequestDTO;
import com.studentautomation.dto.response.StudentSubjectResponseDTO;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.StudentSubject;
import com.studentautomation.entity.Subject;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.repository.StudentSubjectRepository;
import com.studentautomation.repository.SubjectRepository;
import com.studentautomation.service.StudentSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentSubjectServiceImpl implements StudentSubjectService {

    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public StudentSubjectResponseDTO assignSubjectToStudent(StudentSubjectRequestDTO requestDTO) {
        Student student = findStudentById(requestDTO.getStudentId());
        Subject subject = findSubjectById(requestDTO.getSubjectId());

        validateStudentIsActive(student);
        validateSubjectIsActive(subject);

        String normalizedAcademicYear = requestDTO.getAcademicYear().trim();

        Optional<StudentSubject> existingAssignment =
                studentSubjectRepository.findByStudent_IdAndSubject_IdAndAcademicYearIgnoreCase(
                        student.getId(),
                        subject.getId(),
                        normalizedAcademicYear
                );

        if (existingAssignment.isPresent()) {
            StudentSubject assignment = existingAssignment.get();
            if (Boolean.TRUE.equals(assignment.getActive())) {
                throw new DuplicateResourceException(
                        "This subject is already assigned to the student for academic year " + normalizedAcademicYear
                );
            }
            assignment.setActive(true);
            return mapToResponseDTO(studentSubjectRepository.save(assignment));
        }

        StudentSubject studentSubject = StudentSubject.builder()
                .student(student)
                .subject(subject)
                .academicYear(normalizedAcademicYear)
                .active(true)
                .build();

        return mapToResponseDTO(studentSubjectRepository.save(studentSubject));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSubjectResponseDTO> getAllActiveAssignments() {
        return studentSubjectRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSubjectResponseDTO> getAssignmentsByStudentId(Long studentId) {
        findStudentById(studentId);
        return studentSubjectRepository.findByStudent_IdAndActiveTrueOrderBySubject_SubjectNameAsc(studentId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSubjectResponseDTO> getAssignmentsByStudentEmail(String email) {
        Student student = studentRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for logged in user"));

        return studentSubjectRepository.findByStudent_IdAndActiveTrueOrderBySubject_SubjectNameAsc(student.getId())
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSubjectResponseDTO> getAssignmentsBySubjectId(Long subjectId) {
        findSubjectById(subjectId);
        return studentSubjectRepository.findBySubject_IdAndActiveTrueOrderByStudent_NameAsc(subjectId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentSubjectResponseDTO getAssignmentById(Long assignmentId) {
        StudentSubject assignment = findAssignmentById(assignmentId);
        return mapToResponseDTO(assignment);
    }

    @Override
    @Transactional
    public StudentSubjectResponseDTO activateAssignment(Long assignmentId) {
        StudentSubject assignment = findAssignmentById(assignmentId);
        assignment.setActive(true);
        return mapToResponseDTO(studentSubjectRepository.save(assignment));
    }

    @Override
    @Transactional
    public StudentSubjectResponseDTO deactivateAssignment(Long assignmentId) {
        StudentSubject assignment = findAssignmentById(assignmentId);
        if (Boolean.FALSE.equals(assignment.getActive())) {
            throw new InvalidRequestException("Student-subject assignment is already inactive");
        }
        assignment.setActive(false);
        return mapToResponseDTO(studentSubjectRepository.save(assignment));
    }

    @Override
    @Transactional
    public List<StudentSubjectResponseDTO> bulkAssignStudentSubjects(BulkStudentSubjectRequestDTO request) {
        List<StudentSubjectResponseDTO> results = new ArrayList<>();
        for (Long studentId : request.studentIds()) {
            for (Long subjectId : request.subjectIds()) {
                try {
                    StudentSubjectRequestDTO dto = new StudentSubjectRequestDTO();
                    dto.setStudentId(studentId);
                    dto.setSubjectId(subjectId);
                    dto.setAcademicYear(request.academicYear());
                    results.add(assignSubjectToStudent(dto));
                } catch (DuplicateResourceException ignored) {
                    // Skip duplicates in bulk processing
                }
            }
        }
        return results;
    }

    @Override
    @Transactional
    public List<StudentSubjectResponseDTO> assignClassToSubjects(AssignClassSubjectRequestDTO request) {
        List<Student> classStudents = studentRepository.findByDepartmentAndSemester(
                request.department(), request.semester()
        ).stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .filter(s -> request.section() == null || (s.getSection() != null && s.getSection().equalsIgnoreCase(request.section())))
                .toList();

        if (classStudents.isEmpty()) {
            throw new ResourceNotFoundException("No active students found for department " + request.department() + " semester " + request.semester() + " section " + request.section());
        }

        List<StudentSubjectResponseDTO> results = new ArrayList<>();
        for (Student student : classStudents) {
            for (Long subjectId : request.subjectIds()) {
                try {
                    StudentSubjectRequestDTO dto = new StudentSubjectRequestDTO();
                    dto.setStudentId(student.getId());
                    dto.setSubjectId(subjectId);
                    dto.setAcademicYear(request.academicYear());
                    results.add(assignSubjectToStudent(dto));
                } catch (DuplicateResourceException ignored) {
                    // Skip duplicates
                }
            }
        }
        return results;
    }

    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    private Subject findSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));
    }

    private StudentSubject findAssignmentById(Long assignmentId) {
        return studentSubjectRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student-subject assignment not found with ID: " + assignmentId));
    }

    private void validateStudentIsActive(Student student) {
        if (Boolean.FALSE.equals(student.getActive())) {
            throw new InvalidRequestException("Cannot assign a subject to an inactive student");
        }
    }

    private void validateSubjectIsActive(Subject subject) {
        if (Boolean.FALSE.equals(subject.getActive())) {
            throw new InvalidRequestException("Cannot assign an inactive subject to a student");
        }
    }

    private StudentSubjectResponseDTO mapToResponseDTO(StudentSubject assignment) {
        Student student = assignment.getStudent();
        Subject subject = assignment.getSubject();

        return StudentSubjectResponseDTO.builder()
                .id(assignment.getId())
                .studentId(student.getId())
                .studentRegNo(student.getRegNo())
                .studentName(student.getName())
                .studentDepartment(student.getDepartment())
                .studentSemester(student.getSemester())
                .studentSection(student.getSection())
                .subjectId(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .subjectDepartment(subject.getDepartment())
                .subjectSemester(subject.getSemester())
                .academicYear(assignment.getAcademicYear())
                .active(assignment.getActive())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}