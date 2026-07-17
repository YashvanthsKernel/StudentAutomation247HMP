package com.studentautomation.service;

import com.studentautomation.dto.request.TeacherSubjectRequestDTO;
import com.studentautomation.dto.response.TeacherSubjectResponseDTO;

import java.util.List;

/**
 * Service interface for teacher-subject assignment operations.
 *
 * Purpose:
 * Defines business operations for assigning subjects to teachers,
 * retrieving assignments, and deactivating assignments.
 *
 * The implementation is provided by TeacherSubjectServiceImpl.
 *
 * @author Yashvanth
 */
public interface TeacherSubjectService {

    /**
     * Assigns a subject to a teacher for a specific section
     * and academic year.
     *
     * The implementation must:
     * 1. Verify that the teacher exists and is active.
     * 2. Verify that the subject exists and is active.
     * 3. Prevent duplicate active assignments.
     * 4. Reactivate an existing inactive assignment when applicable.
     *
     * @param requestDTO teacher-subject assignment information
     * @return created or reactivated assignment information
     */
    TeacherSubjectResponseDTO assignSubjectToTeacher(
            TeacherSubjectRequestDTO requestDTO
    );

    /**
     * Retrieves all currently active teacher-subject assignments.
     *
     * @return list of active teacher-subject assignments
     */
    List<TeacherSubjectResponseDTO> getAllActiveAssignments();

    /**
     * Retrieves all active subject assignments belonging
     * to a particular teacher.
     *
     * This method can later be used for the teacher dashboard
     * and for validating attendance and marks operations.
     *
     * @param teacherId database ID of the teacher
     * @return active subject assignments belonging to the teacher
     */
    List<TeacherSubjectResponseDTO> getAssignmentsByTeacherId(
            Long teacherId
    );

    /**
     * Retrieves all active teacher assignments belonging
     * to a particular subject.
     *
     * This allows the administrator to identify which teachers
     * handle a subject across different sections.
     *
     * @param subjectId database ID of the subject
     * @return active teacher assignments belonging to the subject
     */
    List<TeacherSubjectResponseDTO> getAssignmentsBySubjectId(
            Long subjectId
    );

    /**
     * Deactivates an existing teacher-subject assignment.
     *
     * This performs a soft delete. The assignment remains
     * stored in the database but cannot be used for attendance,
     * marks, or timetable authorization.
     *
     * @param assignmentId database ID of the assignment
     * @return deactivated assignment information
     */
    TeacherSubjectResponseDTO deactivateAssignment(
            Long assignmentId
    );
}