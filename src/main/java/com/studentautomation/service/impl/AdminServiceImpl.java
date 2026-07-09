package com.studentautomation.service.impl;

import com.studentautomation.dto.request.CreateStudentRequestDTO;
import com.studentautomation.dto.request.CreateTeacherRequestDTO;
import com.studentautomation.dto.response.StudentResponseDTO;
import com.studentautomation.dto.response.TeacherResponseDTO;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.Teacher;
import com.studentautomation.entity.User;
import com.studentautomation.enums.AccountStatus;
import com.studentautomation.enums.Role;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.mapper.StudentMapper;
import com.studentautomation.mapper.TeacherMapper;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.repository.TeacherRepository;
import com.studentautomation.repository.UserRepository;
import com.studentautomation.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for Admin operations.
 *
 * Purpose:
 * This class contains business logic for creating student and teacher accounts.
 * Admin or Super Admin can use these operations.
 *
 * @author Yashvanth
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository,
                            StudentRepository studentRepository,
                            TeacherRepository teacherRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a student login account and student profile.
     *
     * Purpose:
     * This method creates a User with STUDENT role first,
     * then creates a Student profile and links it to that User.
     *
     * @param request student creation request body
     * @return created student response details
     */
    @Override
    @Transactional
    public StudentResponseDTO createStudent(CreateStudentRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (studentRepository.existsByRegNo(request.regNo())) {
            throw new DuplicateResourceException("Register number already exists");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new InvalidRequestException("Password and confirm password do not match");
        }

        User studentUser = new User();
        studentUser.setName(request.name());
        studentUser.setEmail(request.email());
        studentUser.setPassword(passwordEncoder.encode(request.password()));
        studentUser.setRole(Role.STUDENT);
        studentUser.setAccountStatus(AccountStatus.ACTIVE);

        User savedUser = userRepository.save(studentUser);

        Student student = new Student();
        student.setRegNo(request.regNo());
        student.setName(request.name());
        student.setPhoneNo(request.phoneNo());
        student.setDepartment(request.department());
        student.setSemester(request.semester());
        student.setSection(request.section());
        student.setAcademicYear(request.academicYear());
        student.setActive(true);
        student.setUser(savedUser);

        Student savedStudent = studentRepository.save(student);

        return StudentMapper.toResponseDTO(savedStudent);
    }

    /**
     * Creates a teacher login account and teacher profile.
     *
     * Purpose:
     * This method creates a User with TEACHER role first,
     * then creates a Teacher profile and links it to that User.
     *
     * @param request teacher creation request body
     * @return created teacher response details
     */
    @Override
    @Transactional
    public TeacherResponseDTO createTeacher(CreateTeacherRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (teacherRepository.existsByEmployeeId(request.employeeId())) {
            throw new DuplicateResourceException("Employee ID already exists");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new InvalidRequestException("Password and confirm password do not match");
        }

        User teacherUser = new User();
        teacherUser.setName(request.name());
        teacherUser.setEmail(request.email());
        teacherUser.setPassword(passwordEncoder.encode(request.password()));
        teacherUser.setRole(Role.TEACHER);
        teacherUser.setAccountStatus(AccountStatus.ACTIVE);

        User savedUser = userRepository.save(teacherUser);

        Teacher teacher = new Teacher();
        teacher.setEmployeeId(request.employeeId());
        teacher.setName(request.name());
        teacher.setPhoneNo(request.phoneNo());
        teacher.setDepartment(request.department());
        teacher.setDesignation(request.designation());
        teacher.setQualification(request.qualification());
        teacher.setExperienceYears(request.experienceYears());
        teacher.setActive(true);
        teacher.setUser(savedUser);

        Teacher savedTeacher = teacherRepository.save(teacher);

        return TeacherMapper.toResponseDTO(savedTeacher);
    }
}