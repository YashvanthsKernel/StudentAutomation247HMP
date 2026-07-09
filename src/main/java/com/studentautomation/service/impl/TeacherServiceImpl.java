package com.studentautomation.service.impl;

import com.studentautomation.dto.request.TeacherRequestDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.entity.Teacher;
import com.studentautomation.entity.User;
import com.studentautomation.enums.Role;
import com.studentautomation.mapper.TeacherMapper;
import com.studentautomation.repository.TeacherRepository;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.service.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for teacher-related operations.
 *
 * Purpose:
 * This class contains the actual business logic for creating,
 * reading, updating, and deleting teacher profiles.
 *
 * @author Yashvanth
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository,
                              UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new teacher profile and links it with an existing user account.
     *
     * @param request teacher request data from frontend/Postman
     * @return created teacher response data
     */
    @Override
    @Transactional
    public TeacherResponseDTO createTeacher(TeacherRequestDTO request) {

        try {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() != Role.TEACHER) {
                throw new RuntimeException("Only TEACHER role user can be linked with teacher profile");
            }

            if (teacherRepository.existsByEmployeeId(request.employeeId())) {
                throw new RuntimeException("Employee ID already exists");
            }

            if (teacherRepository.existsByUser_Id(request.userId())) {
                throw new RuntimeException("This user is already linked with a teacher profile");
            }

            Teacher teacher = new Teacher();
            teacher.setEmployeeId(request.employeeId());
            teacher.setName(request.name());
            teacher.setPhoneNo(request.phoneNo());
            teacher.setDepartment(request.department());
            teacher.setDesignation(request.designation());
            teacher.setQualification(request.qualification());
            teacher.setExperienceYears(request.experienceYears());
            teacher.setActive(true);
            teacher.setUser(user);

            Teacher savedTeacher = teacherRepository.save(teacher);

            return TeacherMapper.toResponseDTO(savedTeacher);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Teacher creation failed. Please try again later.");
        }
    }

    /**
     * Gets all teacher profiles.
     *
     * @return list of teacher response data
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDTO> getAllTeachers() {

        try {
            return teacherRepository.findAll()
                    .stream()
                    .map(TeacherMapper::toResponseDTO)
                    .toList();

        } catch (Exception exception) {
            throw new RuntimeException("Failed to fetch teachers. Please try again later.");
        }
    }

    /**
     * Gets one teacher by teacher ID.
     *
     * @param id teacher ID
     * @return teacher response data
     */
    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDTO getTeacherById(Long id) {

        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            return TeacherMapper.toResponseDTO(teacher);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Failed to fetch teacher. Please try again later.");
        }
    }

    /**
     * Gets one teacher by employee ID.
     *
     * @param employeeId teacher employee ID
     * @return teacher response data
     */
    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDTO getTeacherByEmployeeId(String employeeId) {

        try {
            Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            return TeacherMapper.toResponseDTO(teacher);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Failed to fetch teacher. Please try again later.");
        }
    }

    /**
     * Updates an existing teacher profile.
     *
     * @param id teacher ID
     * @param request updated teacher request data
     * @return updated teacher response data
     */
    @Override
    @Transactional
    public TeacherResponseDTO updateTeacher(Long id, TeacherRequestDTO request) {

        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            if (teacherRepository.existsByEmployeeIdAndIdNot(request.employeeId(), id)) {
                throw new RuntimeException("Employee ID already exists");
            }

            teacher.setEmployeeId(request.employeeId());
            teacher.setName(request.name());
            teacher.setPhoneNo(request.phoneNo());
            teacher.setDepartment(request.department());
            teacher.setDesignation(request.designation());
            teacher.setQualification(request.qualification());
            teacher.setExperienceYears(request.experienceYears());

            Teacher updatedTeacher = teacherRepository.save(teacher);

            return TeacherMapper.toResponseDTO(updatedTeacher);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Teacher update failed. Please try again later.");
        }
    }

    /**
     * Deactivates a teacher profile instead of permanently deleting it.
     *
     * Purpose:
     * This is soft delete. The teacher record remains in database,
     * but active status becomes false.
     *
     * @param id teacher ID
     */
    @Override
    @Transactional
    public void deleteTeacher(Long id) {

        try {
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            teacher.setActive(false);

            teacherRepository.save(teacher);

        } catch (RuntimeException exception) {
            throw exception;

        } catch (Exception exception) {
            throw new RuntimeException("Teacher delete failed. Please try again later.");
        }
    }
}