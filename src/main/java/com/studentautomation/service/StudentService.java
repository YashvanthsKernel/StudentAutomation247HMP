package com.studentautomation.service;

import com.studentautomation.dto.request.StudentRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;

import java.util.List;

/**
 * Service interface for student-related operations.
 *
 * Purpose:
 * This interface defines what student operations are available
 * in the application.
 *
 * Actual logic will be written inside StudentServiceImpl.
 *
 * @author Yashvanth
 */
public interface StudentService {

    /**
     * Creates a new student profile and links it with a user account.
     *
     * @param request student request data from frontend/Postman
     * @return created student response data
     */
    StudentResponseDTO createStudent(StudentRequestDTO request);

    /**
     * Gets all student profiles.
     *
     * @return list of student response data
     */
    List<StudentResponseDTO> getAllStudents();

    /**
     * Gets one student by student ID.
     *
     * @param id student ID
     * @return student response data
     */
    StudentResponseDTO getStudentById(Long id);

    /**
     * Gets one student by register number.
     *
     * @param regNo student register number
     * @return student response data
     */
    StudentResponseDTO getStudentByRegNo(String regNo);

    /**
     * Updates an existing student profile.
     *
     * @param id student ID
     * @param request updated student request data
     * @return updated student response data
     */
    StudentResponseDTO updateStudent(Long id, StudentRequestDTO request);

    /**
     * Deactivates a student profile.
     *
     * Purpose:
     * Instead of permanently deleting the student from database,
     * we mark active as false.
     *
     * @param id student ID
     */
    void deleteStudent(Long id);

    /**
     * Gets the currently logged-in student's own profile.
     *
     * Purpose:
     * This method is used by Student to view only his/her own profile.
     * The email comes from JWT authentication, not from request body.
     *
     * @param email logged-in student email from JWT
     * @return logged-in student's profile details
     */
    StudentResponseDTO getMyProfile(String email);}