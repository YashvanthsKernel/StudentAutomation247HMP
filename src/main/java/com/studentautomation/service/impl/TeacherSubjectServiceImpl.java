package com.studentautomation.service.impl;

import com.studentautomation.dto.request.TeacherSubjectRequestDTO;
import com.studentautomation.dto.response.TeacherSubjectResponseDTO;
import com.studentautomation.entity.Subject;
import com.studentautomation.entity.Teacher;
import com.studentautomation.entity.TeacherSubject;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.SubjectRepository;
import com.studentautomation.repository.TeacherRepository;
import com.studentautomation.repository.TeacherSubjectRepository;
import com.studentautomation.service.TeacherSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for teacher-subject assignment operations.
 *
 * Purpose:
 * Contains the business logic required to assign subjects to teachers,
 * retrieve active assignments, reactivate old assignments, and
 * deactivate existing assignments.
 *
 * @author Yashvanth
 */
@Service
@RequiredArgsConstructor
public class TeacherSubjectServiceImpl
        implements TeacherSubjectService {

    /**
     * Repository used to access teacher-subject assignments.
     */
    private final TeacherSubjectRepository teacherSubjectRepository;

    /**
     * Repository used to access teacher information.
     */
    private final TeacherRepository teacherRepository;

    /**
     * Repository used to access subject information.
     */
    private final SubjectRepository subjectRepository;

    /**
     * Assigns a subject to a teacher for a section and academic year.
     *
     * This method:
     * 1. Finds the teacher.
     * 2. Finds the subject.
     * 3. Verifies that both are active.
     * 4. Normalizes the section and academic year.
     * 5. Prevents duplicate active assignments.
     * 6. Reactivates an inactive assignment when found.
     * 7. Creates a new assignment when no previous assignment exists.
     *
     * @param requestDTO teacher-subject assignment details
     * @return created or reactivated assignment information
     */
    @Override
    @Transactional
    public TeacherSubjectResponseDTO assignSubjectToTeacher(
            TeacherSubjectRequestDTO requestDTO
    ) {

        Teacher teacher = findTeacherById(
                requestDTO.getTeacherId()
        );

        Subject subject = findSubjectById(
                requestDTO.getSubjectId()
        );

        validateTeacherIsActive(teacher);
        validateSubjectIsActive(subject);

        String normalizedSection =
                normalizeSection(requestDTO.getSection());

        String normalizedAcademicYear =
                normalizeAcademicYear(requestDTO.getAcademicYear());

        validateAcademicYear(normalizedAcademicYear);

        Optional<TeacherSubject> existingAssignment =
                teacherSubjectRepository
                        .findByTeacher_IdAndSubject_IdAndSectionIgnoreCaseAndAcademicYearIgnoreCase(
                                teacher.getId(),
                                subject.getId(),
                                normalizedSection,
                                normalizedAcademicYear
                        );

        /*
         * If the same assignment already exists, determine whether
         * it should be rejected or reactivated.
         */
        if (existingAssignment.isPresent()) {

            TeacherSubject assignment =
                    existingAssignment.get();

            /*
             * An active assignment must not be created twice.
             */
            if (Boolean.TRUE.equals(assignment.getActive())) {
                throw new DuplicateResourceException(
                        "This subject is already assigned to the teacher "
                                + "for section "
                                + normalizedSection
                                + " and academic year "
                                + normalizedAcademicYear
                );
            }

            /*
             * Reactivate the previous inactive assignment instead
             * of inserting another duplicate row.
             */
            assignment.setActive(true);

            TeacherSubject reactivatedAssignment =
                    teacherSubjectRepository.save(assignment);

            return mapToResponseDTO(reactivatedAssignment);
        }

        TeacherSubject teacherSubject =
                TeacherSubject.builder()
                        .teacher(teacher)
                        .subject(subject)
                        .section(normalizedSection)
                        .academicYear(normalizedAcademicYear)
                        .active(true)
                        .build();

        TeacherSubject savedAssignment =
                teacherSubjectRepository.save(teacherSubject);

        return mapToResponseDTO(savedAssignment);
    }

    /**
     * Retrieves all currently active teacher-subject assignments.
     *
     * @return list of active teacher-subject assignments
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectResponseDTO> getAllActiveAssignments() {

        return teacherSubjectRepository
                .findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Retrieves all active subject assignments for a teacher.
     *
     * The teacher is first validated to ensure that the supplied
     * teacher ID exists.
     *
     * @param teacherId database ID of the teacher
     * @return active subject assignments belonging to the teacher
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectResponseDTO> getAssignmentsByTeacherId(
            Long teacherId
    ) {

        findTeacherById(teacherId);

        return teacherSubjectRepository
                .findByTeacher_IdAndActiveTrueOrderBySubject_SubjectNameAsc(
                        teacherId
                )
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Retrieves all active teacher assignments for a subject.
     *
     * The subject is first validated to ensure that the supplied
     * subject ID exists.
     *
     * @param subjectId database ID of the subject
     * @return active teacher assignments belonging to the subject
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectResponseDTO> getAssignmentsBySubjectId(
            Long subjectId
    ) {

        findSubjectById(subjectId);

        return teacherSubjectRepository
                .findBySubject_IdAndActiveTrueOrderByTeacher_NameAsc(
                        subjectId
                )
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Deactivates an existing teacher-subject assignment.
     *
     * This performs a soft delete. The assignment remains in
     * the database but cannot be used for new academic operations.
     *
     * @param assignmentId database ID of the assignment
     * @return deactivated assignment information
     */
    @Override
    @Transactional
    public TeacherSubjectResponseDTO deactivateAssignment(
            Long assignmentId
    ) {

        TeacherSubject assignment =
                findAssignmentById(assignmentId);

        if (Boolean.FALSE.equals(assignment.getActive())) {
            throw new InvalidRequestException(
                    "Teacher-subject assignment is already inactive"
            );
        }

        assignment.setActive(false);

        TeacherSubject deactivatedAssignment =
                teacherSubjectRepository.save(assignment);

        return mapToResponseDTO(deactivatedAssignment);
    }

    /**
     * Finds a teacher using the supplied database ID.
     *
     * @param teacherId database ID of the teacher
     * @return matching teacher entity
     */
    private Teacher findTeacherById(Long teacherId) {

        return teacherRepository
                .findById(teacherId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found with ID: "
                                        + teacherId
                        )
                );
    }

    /**
     * Finds a subject using the supplied database ID.
     *
     * @param subjectId database ID of the subject
     * @return matching subject entity
     */
    private Subject findSubjectById(Long subjectId) {

        return subjectRepository
                .findById(subjectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found with ID: "
                                        + subjectId
                        )
                );
    }

    /**
     * Finds a teacher-subject assignment using its database ID.
     *
     * @param assignmentId database ID of the assignment
     * @return matching teacher-subject assignment
     */
    private TeacherSubject findAssignmentById(Long assignmentId) {

        return teacherSubjectRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher-subject assignment not found "
                                        + "with ID: "
                                        + assignmentId
                        )
                );
    }

    /**
     * Verifies that the teacher is currently active.
     *
     * @param teacher teacher entity to validate
     */
    private void validateTeacherIsActive(Teacher teacher) {

        if (Boolean.FALSE.equals(teacher.getActive())) {
            throw new InvalidRequestException(
                    "Cannot assign a subject to an inactive teacher"
            );
        }
    }

    /**
     * Verifies that the subject is currently active.
     *
     * @param subject subject entity to validate
     */
    private void validateSubjectIsActive(Subject subject) {

        if (Boolean.FALSE.equals(subject.getActive())) {
            throw new InvalidRequestException(
                    "Cannot assign an inactive subject to a teacher"
            );
        }
    }

    /**
     * Converts the section into a standard uppercase format.
     *
     * Example:
     * " a " becomes "A".
     *
     * @param section section received from the request
     * @return normalized section
     */
    private String normalizeSection(String section) {
        return section.trim().toUpperCase();
    }

    /**
     * Removes unwanted spaces from the academic year.
     *
     * Example:
     * " 2026-2027 " becomes "2026-2027".
     *
     * @param academicYear academic year received from the request
     * @return normalized academic year
     */
    private String normalizeAcademicYear(String academicYear) {
        return academicYear.trim();
    }

    /**
     * Verifies that the academic year represents consecutive years.
     *
     * Example:
     * 2026-2027 is valid.
     * 2026-2029 is invalid.
     *
     * @param academicYear normalized academic year
     */
    private void validateAcademicYear(String academicYear) {

        String[] years = academicYear.split("-");

        int startingYear = Integer.parseInt(years[0]);
        int endingYear = Integer.parseInt(years[1]);

        if (endingYear != startingYear + 1) {
            throw new InvalidRequestException(
                    "Academic year must contain consecutive years, "
                            + "for example 2026-2027"
            );
        }
    }

    /**
     * Converts a TeacherSubject entity into a response DTO.
     *
     * Teacher and subject details are read from their related
     * entities without duplicating those values in the mapping table.
     *
     * @param assignment teacher-subject assignment entity
     * @return teacher-subject response DTO
     */
    private TeacherSubjectResponseDTO mapToResponseDTO(
            TeacherSubject assignment
    ) {

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