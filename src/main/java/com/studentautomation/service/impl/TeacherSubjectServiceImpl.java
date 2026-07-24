package com.studentautomation.service.impl;

import com.studentautomation.dto.request.BulkTeacherSubjectRequestDTO;
import com.studentautomation.dto.request.TeacherSubjectRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherSubjectResponseDTO;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.Subject;
import com.studentautomation.entity.Teacher;
import com.studentautomation.entity.TeacherSubject;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.mapper.StudentMapper;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.repository.SubjectRepository;
import com.studentautomation.repository.TeacherRepository;
import com.studentautomation.repository.TeacherSubjectRepository;
import com.studentautomation.service.TeacherSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherSubjectServiceImpl implements TeacherSubjectService {

    private final TeacherSubjectRepository teacherSubjectRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public TeacherSubjectResponseDTO assignSubjectToTeacher(TeacherSubjectRequestDTO requestDTO) {
        Teacher teacher = findTeacherById(requestDTO.getTeacherId());
        Subject subject = findSubjectById(requestDTO.getSubjectId());

        validateTeacherIsActive(teacher);
        validateSubjectIsActive(subject);

        String normalizedSection = requestDTO.getSection().trim().toUpperCase();
        String normalizedAcademicYear = requestDTO.getAcademicYear().trim();

        Optional<TeacherSubject> existingAssignment =
                teacherSubjectRepository.findByTeacher_IdAndSubject_IdAndSectionIgnoreCaseAndAcademicYearIgnoreCase(
                        teacher.getId(), subject.getId(), normalizedSection, normalizedAcademicYear
                );

        if (existingAssignment.isPresent()) {
            TeacherSubject assignment = existingAssignment.get();
            if (Boolean.TRUE.equals(assignment.getActive())) {
                throw new DuplicateResourceException(
                        "This subject is already assigned to the teacher for section " + normalizedSection + " and academic year " + normalizedAcademicYear
                );
            }
            assignment.setActive(true);
            return mapToResponseDTO(teacherSubjectRepository.save(assignment));
        }

        TeacherSubject teacherSubject = TeacherSubject.builder()
                .teacher(teacher)
                .subject(subject)
                .section(normalizedSection)
                .academicYear(normalizedAcademicYear)
                .active(true)
                .build();

        return mapToResponseDTO(teacherSubjectRepository.save(teacherSubject));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectResponseDTO> getAllActiveAssignments() {
        return teacherSubjectRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectResponseDTO> getAssignmentsByTeacherId(Long teacherId) {
        findTeacherById(teacherId);
        return teacherSubjectRepository.findByTeacher_IdAndActiveTrueOrderBySubject_SubjectNameAsc(teacherId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectResponseDTO> getAssignmentsByTeacherEmail(String email) {
        Teacher teacher = teacherRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found for logged in user"));

        return teacherSubjectRepository.findByTeacher_IdAndActiveTrueOrderBySubject_SubjectNameAsc(teacher.getId())
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSectionsForTeacherSubject(String email, Long subjectId) {
        Teacher teacher = teacherRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found for logged in user"));

        return teacherSubjectRepository.findByTeacher_IdAndActiveTrueOrderBySubject_SubjectNameAsc(teacher.getId())
                .stream()
                .filter(ts -> ts.getSubject().getId().equals(subjectId))
                .map(TeacherSubject::getSection)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getStudentsForTeacherSubject(String email, Long subjectId) {
        Teacher teacher = teacherRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found for logged in user"));

        Subject subject = findSubjectById(subjectId);

        List<TeacherSubject> assignments = teacherSubjectRepository.findByTeacher_IdAndActiveTrueOrderBySubject_SubjectNameAsc(teacher.getId())
                .stream()
                .filter(ts -> ts.getSubject().getId().equals(subjectId))
                .toList();

        if (assignments.isEmpty()) {
            throw new InvalidRequestException("Subject is not assigned to this teacher");
        }

        List<Student> students = studentRepository.findByDepartmentAndSemester(subject.getDepartment(), subject.getSemester())
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .toList();

        return students.stream()
                .map(StudentMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectResponseDTO> getAssignmentsBySubjectId(Long subjectId) {
        findSubjectById(subjectId);
        return teacherSubjectRepository.findBySubject_IdAndActiveTrueOrderByTeacher_NameAsc(subjectId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherSubjectResponseDTO getAssignmentById(Long assignmentId) {
        TeacherSubject assignment = findAssignmentById(assignmentId);
        return mapToResponseDTO(assignment);
    }

    @Override
    @Transactional
    public TeacherSubjectResponseDTO activateAssignment(Long assignmentId) {
        TeacherSubject assignment = findAssignmentById(assignmentId);
        assignment.setActive(true);
        return mapToResponseDTO(teacherSubjectRepository.save(assignment));
    }

    @Override
    @Transactional
    public TeacherSubjectResponseDTO deactivateAssignment(Long assignmentId) {
        TeacherSubject assignment = findAssignmentById(assignmentId);
        if (Boolean.FALSE.equals(assignment.getActive())) {
            throw new InvalidRequestException("Teacher-subject assignment is already inactive");
        }
        assignment.setActive(false);
        return mapToResponseDTO(teacherSubjectRepository.save(assignment));
    }

    @Override
    @Transactional
    public List<TeacherSubjectResponseDTO> bulkAssignTeacherSubjects(BulkTeacherSubjectRequestDTO request) {
        List<TeacherSubjectResponseDTO> results = new ArrayList<>();
        for (Long subjectId : request.subjectIds()) {
            try {
                TeacherSubjectRequestDTO dto = new TeacherSubjectRequestDTO();
                dto.setTeacherId(request.teacherId());
                dto.setSubjectId(subjectId);
                dto.setSection(request.section());
                dto.setAcademicYear(request.academicYear());
                results.add(assignSubjectToTeacher(dto));
            } catch (DuplicateResourceException ignored) {
                // Skip duplicates
            }
        }
        return results;
    }

    private Teacher findTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with ID: " + teacherId));
    }

    private Subject findSubjectById(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));
    }

    private TeacherSubject findAssignmentById(Long assignmentId) {
        return teacherSubjectRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher-subject assignment not found with ID: " + assignmentId));
    }

    private void validateTeacherIsActive(Teacher teacher) {
        if (Boolean.FALSE.equals(teacher.getActive())) {
            throw new InvalidRequestException("Cannot assign a subject to an inactive teacher");
        }
    }

    private void validateSubjectIsActive(Subject subject) {
        if (Boolean.FALSE.equals(subject.getActive())) {
            throw new InvalidRequestException("Cannot assign an inactive subject to a teacher");
        }
    }

    private TeacherSubjectResponseDTO mapToResponseDTO(TeacherSubject assignment) {
        Teacher teacher = assignment.getTeacher();
        Subject subject = assignment.getSubject();

        return TeacherSubjectResponseDTO.builder()
                .id(assignment.getId())
                .teacherId(teacher.getId())
                .teacherEmployeeId(teacher.getEmployeeId())
                .teacherName(teacher.getName())
                .subjectId(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .department(subject.getDepartment())
                .semester(subject.getSemester())
                .section(assignment.getSection())
                .academicYear(assignment.getAcademicYear())
                .active(assignment.getActive())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}