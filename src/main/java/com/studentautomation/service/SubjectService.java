package com.studentautomation.service;

import com.studentautomation.dto.request.SubjectRequestDTO;
import com.studentautomation.dto.response.SubjectResponseDTO;

import java.util.List;

/**
 * Service interface for subject-related operations.
 *
 * Purpose:
 * Defines the business operations available for creating,
 * updating, retrieving, activating, and deactivating subjects.
 *
 * The implementation will be provided by SubjectServiceImpl.
 *
 * @author Yashvanth
 */
public interface SubjectService {

    /**
     * Creates a new academic subject.
     *
     * The implementation must prevent duplicate subject codes.
     *
     * @param requestDTO subject information received from the client
     * @return created subject information
     */
    SubjectResponseDTO createSubject(SubjectRequestDTO requestDTO);

    /**
     * Updates an existing academic subject.
     *
     * The implementation must verify that the subject exists
     * and prevent duplicate subject codes.
     *
     * @param subjectId ID of the subject to update
     * @param requestDTO updated subject information
     * @return updated subject information
     */
    SubjectResponseDTO updateSubject(
            Long subjectId,
            SubjectRequestDTO requestDTO
    );

    /**
     * Retrieves a subject using its database ID.
     *
     * @param subjectId subject ID
     * @return matching subject information
     */
    SubjectResponseDTO getSubjectById(Long subjectId);

    /**
     * Retrieves a subject using its unique subject code.
     *
     * @param subjectCode unique subject code
     * @return matching subject information
     */
    SubjectResponseDTO getSubjectByCode(String subjectCode);

    /**
     * Retrieves all currently active subjects.
     *
     * Subjects will be returned in alphabetical order
     * according to their names.
     *
     * @return list of active subjects
     */
    List<SubjectResponseDTO> getAllActiveSubjects();

    /**
     * Retrieves active subjects belonging to the given
     * department and semester.
     *
     * Example:
     * department = CSE
     * semester = 3
     *
     * @param department department name
     * @param semester semester number
     * @return matching active subjects
     */
    List<SubjectResponseDTO> getSubjectsByDepartmentAndSemester(
            String department,
            Integer semester
    );

    /**
     * Activates an existing subject.
     *
     * @param subjectId ID of the subject to activate
     * @return activated subject information
     */
    SubjectResponseDTO activateSubject(Long subjectId);

    /**
     * Deactivates an existing subject.
     *
     * This is a soft-delete operation. The subject record
     * remains in the database but cannot be used for new
     * academic activities.
     *
     * @param subjectId ID of the subject to deactivate
     * @return deactivated subject information
     */
    SubjectResponseDTO deactivateSubject(Long subjectId);
}