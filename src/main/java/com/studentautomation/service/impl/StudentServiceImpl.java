package com.studentautomation.service.impl;

import com.studentautomation.dto.request.StudentRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.User;
import com.studentautomation.enums.Role;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.mapper.StudentMapper;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for student-related operations.
 *
 * Purpose:
 * This class contains business logic for creating, reading,
 * updating, deleting, and self-viewing student profiles.
 *
 * @author Yashvanth
 */
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new student profile and links it with an existing user account.
     *
     * Purpose:
     * This is old/legacy flow.
     * Final flow should use AdminServiceImpl createStudent,
     * where Admin creates both User and Student profile together.
     *
     * @param request student request data from frontend/Postman
     * @return created student response data
     */
    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.STUDENT) {
            throw new InvalidRequestException("Only STUDENT role user can be linked with student profile");
        }

        if (studentRepository.existsByRegNo(request.regNo())) {
            throw new DuplicateResourceException("Register number already exists");
        }

        if (studentRepository.existsByUser_Id(request.userId())) {
            throw new DuplicateResourceException("This user is already linked with a student profile");
        }

        Student student = new Student();
        student.setRegNo(request.regNo());
        student.setName(request.name());
        student.setPhoneNo(request.phoneNo());
        student.setDepartment(request.department());
        student.setSemester(request.semester());
        student.setSection(request.section());
        student.setAcademicYear(request.academicYear());
        student.setActive(true);
        student.setUser(user);

        Student savedStudent = studentRepository.save(student);

        return StudentMapper.toResponseDTO(savedStudent);
    }

    /**
     * Gets all student profiles.
     *
     * @return list of student response data
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::toResponseDTO)
                .toList();
    }

    /**
     * Gets one student by student ID.
     *
     * @param id student ID
     * @return student response data
     */
    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return StudentMapper.toResponseDTO(student);
    }

    /**
     * Gets one student by register number.
     *
     * @param regNo student register number
     * @return student response data
     */
    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByRegNo(String regNo) {

        Student student = studentRepository.findByRegNo(regNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return StudentMapper.toResponseDTO(student);
    }

    /**
     * Updates an existing student profile.
     *
     * @param id student ID
     * @param request updated student request data
     * @return updated student response data
     */
    @Override
    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        if (studentRepository.existsByRegNoAndIdNot(request.regNo(), id)) {
            throw new DuplicateResourceException("Register number already exists");
        }

        student.setRegNo(request.regNo());
        student.setName(request.name());
        student.setPhoneNo(request.phoneNo());
        student.setDepartment(request.department());
        student.setSemester(request.semester());
        student.setSection(request.section());
        student.setAcademicYear(request.academicYear());

        Student updatedStudent = studentRepository.save(student);

        return StudentMapper.toResponseDTO(updatedStudent);
    }

    /**
     * Deactivates a student profile instead of permanently deleting it.
     *
     * Purpose:
     * This is soft delete. The student record remains in database,
     * but active status becomes false.
     *
     * Important:
     * Both the student profile and the linked User account are deactivated
     * so that the user can no longer log in.
     *
     * @param id student ID
     */
    @Override
    @Transactional
    public void deleteStudent(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        student.setActive(false);

        /*
         * Also deactivate the linked User account
         * so the student cannot log in after being deactivated.
         */
        if (student.getUser() != null) {
            student.getUser().setAccountStatus(com.studentautomation.enums.AccountStatus.INACTIVE);
            userRepository.save(student.getUser());
        }

        studentRepository.save(student);
    }

    /**
     * Gets the currently logged-in student's own profile.
     *
     * Purpose:
     * This method finds the student profile using the logged-in user's email.
     * Student cannot pass another student's ID, so data leakage is prevented.
     *
     * @param email logged-in student email from JWT
     * @return logged-in student's profile details
     */
    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getMyProfile(String email) {

        Student student = studentRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        if (Boolean.FALSE.equals(student.getActive())) {
            throw new InvalidRequestException("Student profile is inactive");
        }

        return StudentMapper.toResponseDTO(student);
    }
}