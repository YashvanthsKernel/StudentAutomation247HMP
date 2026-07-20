package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AttendanceRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRecordRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRequestDTO;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.BulkAttendanceErrorDTO;
import com.studentautomation.dto.response.BulkAttendanceResponseDTO;
import com.studentautomation.entity.Attendance;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.Subject;
import com.studentautomation.entity.Teacher;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.repository.AttendanceRepository;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.repository.StudentSubjectRepository;
import com.studentautomation.repository.SubjectRepository;
import com.studentautomation.repository.TeacherRepository;
import com.studentautomation.repository.TeacherSubjectRepository;
import com.studentautomation.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service implementation for attendance-related operations.
 *
 * Purpose:
 * This class contains the business logic for marking attendance,
 * preventing duplicate attendance, validating subject assignments,
 * processing bulk attendance, and retrieving attendance records.
 *
 * Important validations:
 * 1. The teacher profile must exist and be active.
 * 2. The student profile must exist and be active.
 * 3. The subject must exist and be active.
 * 4. The teacher must be assigned to the subject for the student's
 *    section and academic year.
 * 5. The student must be actively assigned to the subject.
 * 6. Duplicate attendance must not exist.
 *
 * @author Yashvanth
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    /**
     * Constructor for injecting required repositories.
     *
     * @param attendanceRepository repository for attendance operations
     * @param studentRepository repository for student operations
     * @param teacherRepository repository for teacher operations
     * @param subjectRepository repository for subject operations
     * @param teacherSubjectRepository repository for teacher-subject assignments
     * @param studentSubjectRepository repository for student-subject assignments
     */
    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            SubjectRepository subjectRepository,
            TeacherSubjectRepository teacherSubjectRepository,
            StudentSubjectRepository studentSubjectRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.teacherSubjectRepository = teacherSubjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    /**
     * Marks attendance for a single student.
     *
     * Purpose:
     * This method fetches the authenticated teacher, student, and subject,
     * validates their active status and assignments, prevents duplicate
     * attendance, and saves the attendance record.
     *
     * @param request attendance request data
     * @param teacherEmail email of the authenticated teacher
     * @return saved attendance details
     */
    @Override
    @Transactional
    public AttendanceResponseDTO markAttendance(
            AttendanceRequestDTO request,
            String teacherEmail
    ) {
        Teacher teacher = getActiveTeacher(teacherEmail);

        Subject subject = getActiveSubject(request.subjectId());

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new InvalidRequestException(
                        "Student not found with ID: " + request.studentId()
                ));

        validateActiveStudent(student);
        validateStudentAcademicDetails(student);

        validateTeacherSubjectAssignment(
                teacher,
                subject,
                student
        );

        validateStudentSubjectAssignment(
                student,
                subject
        );

        boolean alreadyExists = attendanceRepository
                .existsByStudent_IdAndSubject_IdAndAttendanceDateAndPeriodNumber(
                        student.getId(),
                        subject.getId(),
                        request.attendanceDate(),
                        request.periodNumber()
                );

        if (alreadyExists) {
            throw new DuplicateResourceException(
                    "Attendance already marked for this student, subject, date, and period"
            );
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setMarkedBy(teacher);
        attendance.setSubject(subject);
        attendance.setAttendanceDate(request.attendanceDate());
        attendance.setPeriodNumber(request.periodNumber());
        attendance.setStatus(request.status());
        attendance.setRemarks(cleanRemarks(request.remarks()));

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return mapToResponse(savedAttendance);
    }

    /**
     * Marks attendance for multiple students using one request.
     *
     * Purpose:
     * This method validates every student record before saving.
     * The subject and teacher are common for the complete request.
     *
     * Important rule:
     * When any record contains an error, no attendance record
     * from the request is saved.
     *
     * @param request bulk attendance request
     * @param teacherEmail email of the authenticated teacher
     * @return bulk attendance processing result
     */
    @Override
    @Transactional
    public BulkAttendanceResponseDTO markBulkAttendance(
            BulkAttendanceRequestDTO request,
            String teacherEmail
    ) {
        Teacher teacher = getActiveTeacher(teacherEmail);

        Subject subject = getActiveSubject(request.subjectId());

        List<BulkAttendanceErrorDTO> errors = new ArrayList<>();
        List<Attendance> attendanceListToSave = new ArrayList<>();

        /*
         * Stores student IDs already processed inside this request.
         *
         * Purpose:
         * Prevents the same student from appearing multiple times
         * in one bulk attendance request.
         */
        Set<Long> studentIdsInRequest = new HashSet<>();

        for (BulkAttendanceRecordRequestDTO record : request.records()) {

            Long studentId = record.studentId();

            if (studentId == null) {
                errors.add(new BulkAttendanceErrorDTO(
                        null,
                        "studentId",
                        "Student ID is required"
                ));
                continue;
            }

            if (!studentIdsInRequest.add(studentId)) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "studentId",
                        "Duplicate student ID found inside request"
                ));
                continue;
            }

            Student student = studentRepository.findById(studentId)
                    .orElse(null);

            if (student == null) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "studentId",
                        "Student not found"
                ));
                continue;
            }

            if (!Boolean.TRUE.equals(student.getActive())) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "studentId",
                        "Student profile is inactive"
                ));
                continue;
            }

            if (student.getSection() == null
                    || student.getSection().trim().isEmpty()) {

                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "section",
                        "Student section is not configured"
                ));
                continue;
            }

            if (student.getAcademicYear() == null
                    || student.getAcademicYear().trim().isEmpty()) {

                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "academicYear",
                        "Student academic year is not configured"
                ));
                continue;
            }

            if (record.status() == null) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "status",
                        "Attendance status is required"
                ));
                continue;
            }

            boolean teacherAssigned = teacherSubjectRepository
                    .existsByTeacher_IdAndSubject_IdAndSectionIgnoreCaseAndAcademicYearAndActiveTrue(
                            teacher.getId(),
                            subject.getId(),
                            student.getSection().trim(),
                            student.getAcademicYear().trim()
                    );

            if (!teacherAssigned) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "teacherSubjectAssignment",
                        "Teacher is not assigned to this subject for the student's section and academic year"
                ));
                continue;
            }

            boolean studentAssigned = studentSubjectRepository
                    .existsByStudent_IdAndSubject_IdAndAcademicYearAndActiveTrue(
                            student.getId(),
                            subject.getId(),
                            student.getAcademicYear().trim()
                    );

            if (!studentAssigned) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "studentSubjectAssignment",
                        "Student is not actively assigned to this subject"
                ));
                continue;
            }

            boolean alreadyExists = attendanceRepository
                    .existsByStudent_IdAndSubject_IdAndAttendanceDateAndPeriodNumber(
                            student.getId(),
                            subject.getId(),
                            request.attendanceDate(),
                            request.periodNumber()
                    );

            if (alreadyExists) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "attendance",
                        "Attendance already marked for this student, subject, date, and period"
                ));
                continue;
            }

            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setMarkedBy(teacher);
            attendance.setSubject(subject);
            attendance.setAttendanceDate(request.attendanceDate());
            attendance.setPeriodNumber(request.periodNumber());
            attendance.setStatus(record.status());
            attendance.setRemarks(cleanRemarks(record.remarks()));

            attendanceListToSave.add(attendance);
        }

        /*
         * No records are saved when at least one validation error exists.
         *
         * This preserves the all-or-nothing bulk attendance rule.
         */
        if (!errors.isEmpty()) {
            return new BulkAttendanceResponseDTO(
                    request.records().size(),
                    0,
                    errors.size(),
                    errors
            );
        }

        attendanceRepository.saveAll(attendanceListToSave);

        return new BulkAttendanceResponseDTO(
                request.records().size(),
                attendanceListToSave.size(),
                0,
                List.of()
        );
    }

    /**
     * Gets attendance records marked by the authenticated teacher
     * for a specified date.
     *
     * @param attendanceDate attendance date
     * @param teacherEmail email of the authenticated teacher
     * @return attendance records marked by the teacher
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByDateForTeacher(
            LocalDate attendanceDate,
            String teacherEmail
    ) {
        Teacher teacher = getActiveTeacher(teacherEmail);

        return attendanceRepository
                .findByMarkedBy_IdAndAttendanceDate(
                        teacher.getId(),
                        attendanceDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Gets attendance records of the authenticated student.
     *
     * @param studentEmail email of the authenticated student
     * @return attendance records belonging to the student
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getMyAttendance(String studentEmail) {

        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new InvalidRequestException(
                        "Student profile not found"
                ));

        validateActiveStudent(student);

        return attendanceRepository.findByStudent_Id(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Fetches and validates the authenticated teacher.
     *
     * @param teacherEmail email of the authenticated teacher
     * @return active teacher entity
     */
    private Teacher getActiveTeacher(String teacherEmail) {

        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail)
                .orElseThrow(() -> new InvalidRequestException(
                        "Teacher profile not found"
                ));

        if (!Boolean.TRUE.equals(teacher.getActive())) {
            throw new InvalidRequestException(
                    "Teacher profile is inactive"
            );
        }

        return teacher;
    }

    /**
     * Fetches and validates the selected subject.
     *
     * @param subjectId ID of the selected subject
     * @return active subject entity
     */
    private Subject getActiveSubject(Long subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new InvalidRequestException(
                        "Subject not found with ID: " + subjectId
                ));

        if (!Boolean.TRUE.equals(subject.getActive())) {
            throw new InvalidRequestException(
                    "Subject is inactive"
            );
        }

        return subject;
    }

    /**
     * Validates whether a student profile is active.
     *
     * @param student student entity to validate
     */
    private void validateActiveStudent(Student student) {

        if (!Boolean.TRUE.equals(student.getActive())) {
            throw new InvalidRequestException(
                    "Student profile is inactive"
            );
        }
    }

    /**
     * Validates the academic information required for checking
     * teacher-subject assignments.
     *
     * @param student student entity to validate
     */
    private void validateStudentAcademicDetails(Student student) {

        if (student.getSection() == null
                || student.getSection().trim().isEmpty()) {

            throw new InvalidRequestException(
                    "Student section is not configured"
            );
        }

        if (student.getAcademicYear() == null
                || student.getAcademicYear().trim().isEmpty()) {

            throw new InvalidRequestException(
                    "Student academic year is not configured"
            );
        }
    }

    /**
     * Validates whether the teacher is assigned to the selected
     * subject for the student's section and academic year.
     *
     * @param teacher teacher marking attendance
     * @param subject selected subject
     * @param student student whose attendance is being marked
     */
    private void validateTeacherSubjectAssignment(
            Teacher teacher,
            Subject subject,
            Student student
    ) {
        boolean teacherAssigned = teacherSubjectRepository
                .existsByTeacher_IdAndSubject_IdAndSectionIgnoreCaseAndAcademicYearAndActiveTrue(
                        teacher.getId(),
                        subject.getId(),
                        student.getSection().trim(),
                        student.getAcademicYear().trim()
                );

        if (!teacherAssigned) {
            throw new InvalidRequestException(
                    "Teacher is not assigned to subject "
                            + subject.getSubjectCode()
                            + " for section "
                            + student.getSection()
                            + " and academic year "
                            + student.getAcademicYear()
            );
        }
    }

    /**
     * Validates whether the student has an active assignment
     * to the selected subject.
     *
     * @param student student whose attendance is being marked
     * @param subject selected subject
     */
    private void validateStudentSubjectAssignment(
            Student student,
            Subject subject
    ) {
        boolean studentAssigned = studentSubjectRepository
                .existsByStudent_IdAndSubject_IdAndAcademicYearAndActiveTrue(
                        student.getId(),
                        subject.getId(),
                        student.getAcademicYear().trim()
                );

        if (!studentAssigned) {
            throw new InvalidRequestException(
                    "Student is not actively assigned to subject "
                            + subject.getSubjectCode()
                            + " for academic year "
                            + student.getAcademicYear()
            );
        }
    }

    /**
     * Converts an Attendance entity into AttendanceResponseDTO.
     *
     * @param attendance attendance entity
     * @return attendance response DTO
     */
    private AttendanceResponseDTO mapToResponse(Attendance attendance) {

        Student student = attendance.getStudent();
        Teacher teacher = attendance.getMarkedBy();
        Subject subject = attendance.getSubject();

        return new AttendanceResponseDTO(
                attendance.getId(),
                student.getId(),
                student.getName(),
                teacher.getId(),
                teacher.getName(),
                subject.getId(),
                subject.getSubjectCode(),
                subject.getSubjectName(),
                attendance.getAttendanceDate(),
                attendance.getPeriodNumber(),
                attendance.getStatus(),
                attendance.getRemarks(),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt()
        );
    }

    /**
     * Trims optional remarks and converts empty text to null.
     *
     * @param remarks optional attendance remarks
     * @return cleaned remarks or null
     */
    private String cleanRemarks(String remarks) {

        if (remarks == null || remarks.trim().isEmpty()) {
            return null;
        }

        return remarks.trim();
    }
}