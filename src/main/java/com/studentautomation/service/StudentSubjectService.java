package com.studentautomation.service;

import com.studentautomation.dto.request.StudentSubjectRequestDTO;
import com.studentautomation.dto.response.StudentSubjectResponseDTO;

import java.util.List;

/**
 * Service interface for student-subject assignment operations.
 *
 * Purpose:
 * Defines operations for assigning subjects to students,
 * retrieving active assignments, and deactivating assignments.
 *
 * The actual business logic will be implemented inside
 * StudentSubjectServiceImpl.
 *
 * @author Yashvanth
 */
public interface StudentSubjectService {

    /**
     * Assigns a subject to a student for a particular academic year.
     *
     * The implementation must:
     * 1. Verify that the student exists.
     * 2. Verify that the subject exists.
     * 3. Verify that both are active.
     * 4. Validate department and semester compatibility.
     * 5. Prevent duplicate active assignments.
     * 6. Reactivate an existing inactive assignment when applicable.
     *
     * @param requestDTO student-subject assignment information
     * @return created or reactivated assignment information
     */
    StudentSubjectResponseDTO assignSubjectToStudent(
            StudentSubjectRequestDTO requestDTO
    );

    /**
     * Retrieves all currently active student-subject assignments.
     *
     * @return list of active student-subject assignments
     */
    List<StudentSubjectResponseDTO> getAllActiveAssignments();

    /**
     * Retrieves all active subject assignments belonging
     * to a particular student.
     *
     * This method can later be used for the student dashboard,
     * attendance, marks, and timetable modules.
     *
     * @param studentId database ID of the student
     * @return active subject assignments belonging to the student
     */
    List<StudentSubjectResponseDTO> getAssignmentsByStudentId(
            Long studentId
    );

    /**
     * Retrieves all active student assignments belonging
     * to a particular subject.
     *
     * This method allows an admin or teacher to see which students
     * are assigned to a particular subject.
     *
     * @param subjectId database ID of the subject
     * @return active student assignments belonging to the subject
     */
    List<StudentSubjectResponseDTO> getAssignmentsBySubjectId(
            Long subjectId
    );

    /**
     * Deactivates an existing student-subject assignment.
     *
     * This performs a soft delete. The assignment remains
     * stored in the database for academic history.
     *
     * @param assignmentId database ID of the assignment
     * @return deactivated assignment information
     */
    StudentSubjectResponseDTO deactivateAssignment(
            Long assignmentId
    );
}