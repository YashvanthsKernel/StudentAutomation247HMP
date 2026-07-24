package com.studentautomation.service.impl;

import com.studentautomation.dto.request.TeacherRequestDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.entity.Teacher;
import com.studentautomation.entity.User;
import com.studentautomation.enums.Role;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
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
 * This class contains business logic for creating, reading,
 * updating, deleting, and self-viewing teacher profiles.
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
     * Purpose:
     * This is old/legacy flow.
     * Final flow should use AdminServiceImpl createTeacher,
     * where Admin creates both User and Teacher profile together.
     *
     * @param request teacher request data from frontend/Postman
     * @return created teacher response data
     */
    @Override
    @Transactional
    public TeacherResponseDTO createTeacher(TeacherRequestDTO request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.TEACHER) {
            throw new InvalidRequestException("Only TEACHER role user can be linked with teacher profile");
        }

        if (teacherRepository.existsByEmployeeId(request.employeeId())) {
            throw new DuplicateResourceException("Employee ID already exists");
        }

        if (teacherRepository.existsByUser_Id(request.userId())) {
            throw new DuplicateResourceException("This user is already linked with a teacher profile");
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
    }

    /**
     * Gets all teacher profiles.
     *
     * @return list of teacher response data
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponseDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(TeacherMapper::toResponseDTO)
                .toList();
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

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        return TeacherMapper.toResponseDTO(teacher);
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

        Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        return TeacherMapper.toResponseDTO(teacher);
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

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        if (teacherRepository.existsByEmployeeIdAndIdNot(request.employeeId(), id)) {
            throw new DuplicateResourceException("Employee ID already exists");
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
    }

    /**
     * Deactivates a teacher profile instead of permanently deleting it.
     *
     * Purpose:
     * This is soft delete. The teacher record remains in database,
     * but active status becomes false.
     *
     * Important:
     * Both the teacher profile and the linked User account are deactivated
     * so that the user can no longer log in.
     *
     * @param id teacher ID
     */
    @Override
    @Transactional
    public void deleteTeacher(Long id) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        teacher.setActive(false);

        /*
         * Also deactivate the linked User account
         * so the teacher cannot log in after being deactivated.
         */
        if (teacher.getUser() != null) {
            teacher.getUser().setAccountStatus(com.studentautomation.enums.AccountStatus.INACTIVE);
            userRepository.save(teacher.getUser());
        }

        teacherRepository.save(teacher);
    }

    /**
     * Gets the currently logged-in teacher's own profile.
     *
     * Purpose:
     * This method finds the teacher profile using the logged-in user's email.
     * Teacher cannot pass another teacher's ID, so data leakage is prevented.
     *
     * @param email logged-in teacher email from JWT
     * @return logged-in teacher's profile details
     */
    @Override
    @Transactional(readOnly = true)
    public TeacherResponseDTO getMyProfile(String email) {

        Teacher teacher = teacherRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        if (Boolean.FALSE.equals(teacher.getActive())) {
            throw new InvalidRequestException("Teacher profile is inactive");
        }

        return TeacherMapper.toResponseDTO(teacher);
    }
}