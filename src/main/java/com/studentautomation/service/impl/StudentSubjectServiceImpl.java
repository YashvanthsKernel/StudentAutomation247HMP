package com.studentautomation.service.impl;

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

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for student-subject assignment operations.
 *
 * Purpose:
 * Contains business logic for assigning subjects to students,
 * retrieving active assignments, preventing duplicates,
 * reactivating old assignments, and deactivating assignments.
 *
 * @author Yashvanth
 */
@Service
@RequiredArgsConstructor
public class StudentSubjectServiceImpl
        implements StudentSubjectService {

    /**
     * Repository used to perform student-subject
     * assignment database operations.
     */
    private final StudentSubjectRepository studentSubjectRepository;

    /**
     * Repository used to access student information.
     */
    private final StudentRepository studentRepository;

    /**
     * Repository used to access subject information.
     */
    private final SubjectRepository subjectRepository;

    /**
     * Assigns a subject to a student for an academic year.
     *
     * This method:
     * 1. Finds the student.
     * 2. Finds the subject.
     * 3. Verifies that both are active.
     * 4. Checks department compatibility.
     * 5. Checks semester compatibility.
     * 6. Checks academic-year compatibility.
     * 7. Prevents duplicate active assignments.
     * 8. Reactivates an existing inactive assignment.
     * 9. Creates a new assignment when no previous row exists.
     *
     * @param requestDTO student-subject assignment information
     * @return created or reactivated assignment information
     */
    @Override
    @Transactional
    public StudentSubjectResponseDTO assignSubjectToStudent(
            StudentSubjectRequestDTO requestDTO
    ) {

        Student student = findStudentById(
                requestDTO.getStudentId()
        );

        Subject subject = findSubjectById(
                requestDTO.getSubjectId()
        );

        validateStudentIsActive(student);
        validateSubjectIsActive(subject);
        validateDepartment(student, subject);
        validateSemester(student, subject);

        String normalizedAcademicYear =
                normalizeAcademicYear(
                        requestDTO.getAcademicYear()
                );

        validateAcademicYear(normalizedAcademicYear);

        validateStudentAcademicYear(
                student,
                normalizedAcademicYear
        );

        Optional<StudentSubject> existingAssignment =
                studentSubjectRepository
                        .findByStudent_IdAndSubject_IdAndAcademicYearIgnoreCase(
                                student.getId(),
                                subject.getId(),
                                normalizedAcademicYear
                        );

        /*
         * If the same assignment already exists, either reject
         * the duplicate or reactivate the inactive record.
         */
        if (existingAssignment.isPresent()) {

            StudentSubject assignment =
                    existingAssignment.get();

            /*
             * Prevents the same active assignment
             * from being created twice.
             */
            if (Boolean.TRUE.equals(assignment.getActive())) {
                throw new DuplicateResourceException(
                        "This subject is already assigned to the student "
                                + "for academic year "
                                + normalizedAcademicYear
                );
            }

            /*
             * Reactivates the old row instead of creating
             * another duplicate database record.
             */
            assignment.setActive(true);

            StudentSubject reactivatedAssignment =
                    studentSubjectRepository.save(assignment);

            return mapToResponseDTO(reactivatedAssignment);
        }

        StudentSubject studentSubject =
                StudentSubject.builder()
                        .student(student)
                        .subject(subject)
                        .academicYear(normalizedAcademicYear)
                        .active(true)
                        .build();

        StudentSubject savedAssignment =
                studentSubjectRepository.save(studentSubject);

        return mapToResponseDTO(savedAssignment);
    }

    /**
     * Retrieves all currently active student-subject assignments.
     *
     * @return list of active assignments
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentSubjectResponseDTO> getAllActiveAssignments() {

        return studentSubjectRepository
                .findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Retrieves all active subject assignments belonging
     * to a particular student.
     *
     * The student is validated before retrieving assignments.
     *
     * @param studentId database ID of the student
     * @return active assignments belonging to the student
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentSubjectResponseDTO> getAssignmentsByStudentId(
            Long studentId
    ) {

        findStudentById(studentId);

        return studentSubjectRepository
                .findByStudent_IdAndActiveTrueOrderBySubject_SubjectNameAsc(
                        studentId
                )
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Retrieves all active student assignments belonging
     * to a particular subject.
     *
     * The subject is validated before retrieving assignments.
     *
     * @param subjectId database ID of the subject
     * @return active assignments belonging to the subject
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentSubjectResponseDTO> getAssignmentsBySubjectId(
            Long subjectId
    ) {

        findSubjectById(subjectId);

        return studentSubjectRepository
                .findBySubject_IdAndActiveTrueOrderByStudent_NameAsc(
                        subjectId
                )
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Deactivates an existing student-subject assignment.
     *
     * This performs a soft delete. The assignment remains
     * stored in the database for academic history.
     *
     * @param assignmentId database ID of the assignment
     * @return deactivated assignment information
     */
    @Override
    @Transactional
    public StudentSubjectResponseDTO deactivateAssignment(
            Long assignmentId
    ) {

        StudentSubject assignment =
                findAssignmentById(assignmentId);

        if (Boolean.FALSE.equals(assignment.getActive())) {
            throw new InvalidRequestException(
                    "Student-subject assignment is already inactive"
            );
        }

        assignment.setActive(false);

        StudentSubject deactivatedAssignment =
                studentSubjectRepository.save(assignment);

        return mapToResponseDTO(deactivatedAssignment);
    }

    /**
     * Finds a student using the supplied database ID.
     *
     * @param studentId database ID of the student
     * @return matching student entity
     */
    private Student findStudentById(Long studentId) {

        return studentRepository
                .findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: "
                                        + studentId
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
     * Finds a student-subject assignment using its database ID.
     *
     * @param assignmentId database ID of the assignment
     * @return matching assignment entity
     */
    private StudentSubject findAssignmentById(Long assignmentId) {

        return studentSubjectRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student-subject assignment not found "
                                        + "with ID: "
                                        + assignmentId
                        )
                );
    }

    /**
     * Verifies that the student is currently active.
     *
     * @param student student entity to validate
     */
    private void validateStudentIsActive(Student student) {

        if (Boolean.FALSE.equals(student.getActive())) {
            throw new InvalidRequestException(
                    "Cannot assign a subject to an inactive student"
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
                    "Cannot assign an inactive subject to a student"
            );
        }
    }

    /**
     * Verifies that the student's department matches
     * the subject's department.
     *
     * Example:
     * A CSE student can receive a CSE subject.
     *
     * @param student student entity
     * @param subject subject entity
     */
    private void validateDepartment(
            Student student,
            Subject subject
    ) {

        boolean departmentMatches =
                student.getDepartment()
                        .trim()
                        .equalsIgnoreCase(
                                subject.getDepartment().trim()
                        );

        if (!departmentMatches) {
            throw new InvalidRequestException(
                    "Student department "
                            + student.getDepartment()
                            + " does not match subject department "
                            + subject.getDepartment()
            );
        }
    }

    /**
     * Verifies that the student's semester matches
     * the semester of the subject.
     *
     * @param student student entity
     * @param subject subject entity
     */
    private void validateSemester(
            Student student,
            Subject subject
    ) {

        boolean semesterMatches =
                student.getSemester()
                        .equals(subject.getSemester());

        if (!semesterMatches) {
            throw new InvalidRequestException(
                    "Student semester "
                            + student.getSemester()
                            + " does not match subject semester "
                            + subject.getSemester()
            );
        }
    }

    /**
     * Verifies that the assignment academic year matches
     * the academic year stored in the student profile.
     *
     * @param student student entity
     * @param academicYear requested academic year
     */
    private void validateStudentAcademicYear(
            Student student,
            String academicYear
    ) {

        boolean academicYearMatches =
                student.getAcademicYear()
                        .trim()
                        .equalsIgnoreCase(academicYear);

        if (!academicYearMatches) {
            throw new InvalidRequestException(
                    "Student academic year "
                            + student.getAcademicYear()
                            + " does not match assignment academic year "
                            + academicYear
            );
        }
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
     * Verifies that the academic year contains consecutive years.
     *
     * Valid:
     * 2026-2027
     *
     * Invalid:
     * 2026-2029
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
     * Converts a StudentSubject entity into
     * StudentSubjectResponseDTO.
     *
     * @param assignment student-subject assignment entity
     * @return student-subject response DTO
     */
    private StudentSubjectResponseDTO mapToResponseDTO(
            StudentSubject assignment
    ) {

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