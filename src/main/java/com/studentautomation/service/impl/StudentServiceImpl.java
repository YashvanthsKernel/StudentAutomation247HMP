package com.studentautomation.service.impl;

import com.studentautomation.dto.request.StudentRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.User;
import com.studentautomation.enums.Role;
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
 * This class contains the actual business logic for creating,
 * reading, updating, and deleting student profiles.
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
     * @param request student request data from frontend/Postman
     * @return created student response data
     */
    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO request) {

        try {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() != Role.STUDENT) {
                throw new RuntimeException("Only STUDENT role user can be linked with student profile");
            }

            if (studentRepository.existsByRegNo(request.regNo())) {
                throw new RuntimeException("Register number already exists");
            }

            if (studentRepository.existsByUser_Id(request.userId())) {
                throw new RuntimeException("This user is already linked with a student profile");
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

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Student creation failed. Please try again later.");
        }
    }

    /**
     * Gets all student profiles.
     *
     * @return list of student response data
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {

        try {
            return studentRepository.findAll()
                    .stream()
                    .map(StudentMapper::toResponseDTO)
                    .toList();

        } catch (Exception exception) {
            throw new RuntimeException("Failed to fetch students. Please try again later.");
        }
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

        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            return StudentMapper.toResponseDTO(student);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Failed to fetch student. Please try again later.");
        }
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

        try {
            Student student = studentRepository.findByRegNo(regNo)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            return StudentMapper.toResponseDTO(student);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Failed to fetch student. Please try again later.");
        }
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

        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            if (studentRepository.existsByRegNoAndIdNot(request.regNo(), id)) {
                throw new RuntimeException("Register number already exists");
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

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Student update failed. Please try again later.");
        }
    }

    /**
     * Deactivates a student profile instead of permanently deleting it.
     *
     * Purpose:
     * This is soft delete. The student record remains in database,
     * but active status becomes false.
     *
     * @param id student ID
     */
    @Override
    @Transactional
    public void deleteStudent(Long id) {

        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            student.setActive(false);

            studentRepository.save(student);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Student delete failed. Please try again later.");
        }
    }
}