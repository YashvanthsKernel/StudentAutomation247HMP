package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AdminResetPasswordRequestDTO;
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
import com.studentautomation.exception.ResourceNotFoundException;
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
 * This class contains business logic for creating student and teacher accounts
 * and managing their account lifecycle (activate, deactivate, block, unblock,
 * reset password).
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

    // ─── Student Account Management ──────────────────────────────────────

    /**
     * Activates a student profile and their linked login account.
     *
     * Purpose:
     * Sets student.active = true and user.accountStatus = ACTIVE
     * so the student can log in again.
     *
     * @param studentId student ID
     */
    @Override
    @Transactional
    public void activateStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setActive(true);
        studentRepository.save(student);

        if (student.getUser() != null) {
            student.getUser().setAccountStatus(AccountStatus.ACTIVE);
            userRepository.save(student.getUser());
        }
    }

    /**
     * Deactivates a student profile and their linked login account.
     *
     * Purpose:
     * Sets student.active = false and user.accountStatus = INACTIVE
     * so the student can no longer log in.
     *
     * @param studentId student ID
     */
    @Override
    @Transactional
    public void deactivateStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setActive(false);
        studentRepository.save(student);

        if (student.getUser() != null) {
            student.getUser().setAccountStatus(AccountStatus.INACTIVE);
            userRepository.save(student.getUser());
        }
    }

    /**
     * Blocks a student's login account.
     *
     * Purpose:
     * Sets user.accountStatus = BLOCKED.
     * Student profile remains active, but login is blocked.
     * Use this for disciplinary actions.
     *
     * @param studentId student ID
     */
    @Override
    @Transactional
    public void blockStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getUser() == null) {
            throw new InvalidRequestException("Student has no linked user account");
        }

        if (student.getUser().getAccountStatus() == AccountStatus.BLOCKED) {
            throw new InvalidRequestException("Student account is already blocked");
        }

        student.getUser().setAccountStatus(AccountStatus.BLOCKED);
        userRepository.save(student.getUser());
    }

    /**
     * Unblocks a blocked student's login account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so student can log in again.
     *
     * @param studentId student ID
     */
    @Override
    @Transactional
    public void unblockStudent(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getUser() == null) {
            throw new InvalidRequestException("Student has no linked user account");
        }

        if (student.getUser().getAccountStatus() != AccountStatus.BLOCKED) {
            throw new InvalidRequestException("Student account is not blocked");
        }

        student.getUser().setAccountStatus(AccountStatus.ACTIVE);
        student.setActive(true);
        userRepository.save(student.getUser());
        studentRepository.save(student);
    }

    /**
     * Resets a student's login password.
     *
     * Purpose:
     * Admin sets a new temporary password for the student.
     * Student should change this password after logging in.
     *
     * @param studentId student ID
     * @param request   new password and confirm password
     */
    @Override
    @Transactional
    public void resetStudentPassword(Long studentId, AdminResetPasswordRequestDTO request) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getUser() == null) {
            throw new InvalidRequestException("Student has no linked user account");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidRequestException("New password and confirm password do not match");
        }

        student.getUser().setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(student.getUser());
    }

    // ─── Teacher Account Management ──────────────────────────────────────

    /**
     * Activates a teacher profile and their linked login account.
     *
     * Purpose:
     * Sets teacher.active = true and user.accountStatus = ACTIVE
     * so the teacher can log in again.
     *
     * @param teacherId teacher ID
     */
    @Override
    @Transactional
    public void activateTeacher(Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        teacher.setActive(true);
        teacherRepository.save(teacher);

        if (teacher.getUser() != null) {
            teacher.getUser().setAccountStatus(AccountStatus.ACTIVE);
            userRepository.save(teacher.getUser());
        }
    }

    /**
     * Deactivates a teacher profile and their linked login account.
     *
     * Purpose:
     * Sets teacher.active = false and user.accountStatus = INACTIVE
     * so the teacher can no longer log in.
     *
     * @param teacherId teacher ID
     */
    @Override
    @Transactional
    public void deactivateTeacher(Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        teacher.setActive(false);
        teacherRepository.save(teacher);

        if (teacher.getUser() != null) {
            teacher.getUser().setAccountStatus(AccountStatus.INACTIVE);
            userRepository.save(teacher.getUser());
        }
    }

    /**
     * Blocks a teacher's login account.
     *
     * Purpose:
     * Sets user.accountStatus = BLOCKED.
     * Teacher profile remains active, but login is blocked.
     *
     * @param teacherId teacher ID
     */
    @Override
    @Transactional
    public void blockTeacher(Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getUser() == null) {
            throw new InvalidRequestException("Teacher has no linked user account");
        }

        if (teacher.getUser().getAccountStatus() == AccountStatus.BLOCKED) {
            throw new InvalidRequestException("Teacher account is already blocked");
        }

        teacher.getUser().setAccountStatus(AccountStatus.BLOCKED);
        userRepository.save(teacher.getUser());
    }

    /**
     * Unblocks a blocked teacher's login account.
     *
     * Purpose:
     * Sets user.accountStatus = ACTIVE so teacher can log in again.
     *
     * @param teacherId teacher ID
     */
    @Override
    @Transactional
    public void unblockTeacher(Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getUser() == null) {
            throw new InvalidRequestException("Teacher has no linked user account");
        }

        if (teacher.getUser().getAccountStatus() != AccountStatus.BLOCKED) {
            throw new InvalidRequestException("Teacher account is not blocked");
        }

        teacher.getUser().setAccountStatus(AccountStatus.ACTIVE);
        teacher.setActive(true);
        userRepository.save(teacher.getUser());
        teacherRepository.save(teacher);
    }

    /**
     * Resets a teacher's login password.
     *
     * Purpose:
     * Admin sets a new temporary password for the teacher.
     * Teacher should change this password after logging in.
     *
     * @param teacherId teacher ID
     * @param request   new password and confirm password
     */
    @Override
    @Transactional
    public void resetTeacherPassword(Long teacherId, AdminResetPasswordRequestDTO request) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getUser() == null) {
            throw new InvalidRequestException("Teacher has no linked user account");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidRequestException("New password and confirm password do not match");
        }

        teacher.getUser().setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(teacher.getUser());
    }
}