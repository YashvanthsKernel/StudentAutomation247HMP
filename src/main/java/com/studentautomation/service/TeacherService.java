package com.studentautomation.service;

import com.studentautomation.dto.request.TeacherRequestDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;

import java.util.List;

/**
 * Service interface for teacher-related operations.
 *
 * Purpose:
 * This interface defines what teacher operations are available
 * in the application.
 *
 * Actual logic will be written inside TeacherServiceImpl.
 *
 * @author Yashvanth
 */
public interface TeacherService {

    /**
     * Creates a new teacher profile and links it with a user account.
     *
     * @param request teacher request data from frontend/Postman
     * @return created teacher response data
     */
    TeacherResponseDTO createTeacher(TeacherRequestDTO request);

    /**
     * Gets all teacher profiles.
     *
     * @return list of teacher response data
     */
    List<TeacherResponseDTO> getAllTeachers();

    /**
     * Gets one teacher by teacher ID.
     *
     * @param id teacher ID
     * @return teacher response data
     */
    TeacherResponseDTO getTeacherById(Long id);

    /**
     * Gets one teacher by employee ID.
     *
     * @param employeeId teacher employee ID
     * @return teacher response data
     */
    TeacherResponseDTO getTeacherByEmployeeId(String employeeId);

    /**
     * Updates an existing teacher profile.
     *
     * @param id teacher ID
     * @param request updated teacher request data
     * @return updated teacher response data
     */
    TeacherResponseDTO updateTeacher(Long id, TeacherRequestDTO request);

    /**
     * Deactivates a teacher profile.
     *
     * Purpose:
     * Instead of permanently deleting the teacher from database,
     * we mark active as false.
     *
     * @param id teacher ID
     */
    void deleteTeacher(Long id);
}