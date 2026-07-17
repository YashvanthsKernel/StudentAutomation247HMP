package com.studentautomation.service.impl;

import com.studentautomation.dto.request.SubjectRequestDTO;
import com.studentautomation.dto.response.SubjectResponseDTO;
import com.studentautomation.entity.Subject;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.repository.SubjectRepository;
import com.studentautomation.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for subject-related operations.
 *
 * Purpose:
 * Contains business logic for creating, updating, retrieving,
 * activating, and deactivating academic subjects.
 *
 * @author Yashvanth
 */
@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    /**
     * Repository used to perform subject database operations.
     *
     * Because this field is final, Lombok's RequiredArgsConstructor
     * automatically creates the required constructor.
     */
    private final SubjectRepository subjectRepository;

    /**
     * Creates a new academic subject.
     *
     * This method:
     * 1. Normalizes the request data.
     * 2. Prevents duplicate subject codes.
     * 3. Creates and saves the subject.
     * 4. Converts the saved entity into a response DTO.
     *
     * @param requestDTO subject information received from the client
     * @return created subject information
     */
    @Override
    @Transactional
    public SubjectResponseDTO createSubject(SubjectRequestDTO requestDTO) {

        String normalizedSubjectCode = normalizeSubjectCode(requestDTO.getSubjectCode());

        if (subjectRepository.existsBySubjectCodeIgnoreCase(
                normalizedSubjectCode
        )) {
            throw new DuplicateResourceException(
                    "Subject already exists with code: "
                            + normalizedSubjectCode
            );
        }

        Subject subject = Subject.builder()
                .subjectCode(normalizedSubjectCode)
                .subjectName(requestDTO.getSubjectName().trim())
                .department(normalizeDepartment(requestDTO.getDepartment()))
                .semester(requestDTO.getSemester())
                .credits(requestDTO.getCredits())
                .active(true)
                .build();

        Subject savedSubject = subjectRepository.save(subject);

        return mapToResponseDTO(savedSubject);
    }

    /**
     * Updates an existing academic subject.
     *
     * The method checks whether the subject exists and ensures
     * that another subject does not use the updated subject code.
     *
     * @param subjectId ID of the subject to update
     * @param requestDTO updated subject information
     * @return updated subject information
     */
    @Override
    @Transactional
    public SubjectResponseDTO updateSubject(
            Long subjectId,
            SubjectRequestDTO requestDTO
    ) {

        Subject existingSubject = findSubjectById(subjectId);

        String normalizedSubjectCode = normalizeSubjectCode(requestDTO.getSubjectCode());

        boolean duplicateSubjectCode =
                subjectRepository
                        .existsBySubjectCodeIgnoreCaseAndIdNot(
                                normalizedSubjectCode,
                                subjectId
                        );

        if (duplicateSubjectCode) {
            throw new DuplicateResourceException(
                    "Another subject already exists with code: "
                            + normalizedSubjectCode
            );
        }

        existingSubject.setSubjectCode(normalizedSubjectCode);
        existingSubject.setSubjectName(
                requestDTO.getSubjectName().trim()
        );
        existingSubject.setDepartment(
                normalizeDepartment(requestDTO.getDepartment())
        );
        existingSubject.setSemester(requestDTO.getSemester());
        existingSubject.setCredits(requestDTO.getCredits());

        Subject updatedSubject =
                subjectRepository.save(existingSubject);

        return mapToResponseDTO(updatedSubject);
    }

    /**
     * Retrieves a subject using its database ID.
     *
     * @param subjectId subject ID
     * @return matching subject information
     */
    @Override
    @Transactional(readOnly = true)
    public SubjectResponseDTO getSubjectById(Long subjectId) {

        Subject subject = findSubjectById(subjectId);

        return mapToResponseDTO(subject);
    }

    /**
     * Retrieves a subject using its unique subject code.
     *
     * @param subjectCode unique subject code
     * @return matching subject information
     */
    @Override
    @Transactional(readOnly = true)
    public SubjectResponseDTO getSubjectByCode(String subjectCode) {

        String normalizedSubjectCode =
                normalizeSubjectCode(subjectCode);

        Subject subject = subjectRepository
                .findBySubjectCodeIgnoreCase(normalizedSubjectCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found with code: "
                                        + normalizedSubjectCode
                        )
                );

        return mapToResponseDTO(subject);
    }

    /**
     * Retrieves all active academic subjects.
     *
     * @return list of active subjects
     */
    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDTO> getAllActiveSubjects() {

        return subjectRepository
                .findByActiveTrueOrderBySubjectNameAsc()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Retrieves active subjects by department and semester.
     *
     * @param department department name
     * @param semester semester number
     * @return matching active subjects
     */
    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponseDTO>
    getSubjectsByDepartmentAndSemester(
            String department,
            Integer semester
    ) {

        String normalizedDepartment =
                normalizeDepartment(department);

        return subjectRepository
                .findByDepartmentIgnoreCaseAndSemesterAndActiveTrueOrderBySubjectNameAsc(
                        normalizedDepartment,
                        semester
                )
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Activates an existing academic subject.
     *
     * @param subjectId ID of the subject to activate
     * @return activated subject information
     */
    @Override
    @Transactional
    public SubjectResponseDTO activateSubject(Long subjectId) {

        Subject subject = findSubjectById(subjectId);

        subject.setActive(true);

        Subject activatedSubject =
                subjectRepository.save(subject);

        return mapToResponseDTO(activatedSubject);
    }

    /**
     * Deactivates an existing academic subject.
     *
     * This is a soft-delete operation. The subject remains
     * stored in the database.
     *
     * @param subjectId ID of the subject to deactivate
     * @return deactivated subject information
     */
    @Override
    @Transactional
    public SubjectResponseDTO deactivateSubject(Long subjectId) {

        Subject subject = findSubjectById(subjectId);

        subject.setActive(false);

        Subject deactivatedSubject =
                subjectRepository.save(subject);

        return mapToResponseDTO(deactivatedSubject);
    }

    /**
     * Finds a subject entity using its database ID.
     *
     * This private helper avoids repeating the same
     * subject-not-found logic in multiple methods.
     *
     * @param subjectId subject ID
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
     * Converts a Subject entity into SubjectResponseDTO.
     *
     * @param subject subject entity
     * @return subject response DTO
     */
    private SubjectResponseDTO mapToResponseDTO(Subject subject) {

        return SubjectResponseDTO.builder()
                .id(subject.getId())
                .subjectCode(subject.getSubjectCode())
                .subjectName(subject.getSubjectName())
                .department(subject.getDepartment())
                .semester(subject.getSemester())
                .credits(subject.getCredits())
                .active(subject.getActive())
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt())
                .build();
    }

    /**
     * Removes extra spaces and converts a subject code
     * into uppercase.
     *
     * Example:
     * " cs301 " becomes "CS301".
     *
     * @param subjectCode subject code received from the client
     * @return normalized subject code
     */
    private String normalizeSubjectCode(String subjectCode) {
        return subjectCode.trim().toUpperCase();
    }

    /**
     * Removes extra spaces and converts a department
     * name into uppercase.
     *
     * Example:
     * " cse " becomes "CSE".
     *
     * @param department department received from the client
     * @return normalized department name
     */
    private String normalizeDepartment(String department) {
        return department.trim().toUpperCase();
    }
}