package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AttendanceRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRecordRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRequestDTO;
import com.studentautomation.dto.response.AttendanceResponseDTO;
import com.studentautomation.dto.response.BulkAttendanceErrorDTO;
import com.studentautomation.dto.response.BulkAttendanceResponseDTO;
import com.studentautomation.entity.Attendance;
import com.studentautomation.entity.Student;
import com.studentautomation.entity.Teacher;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.repository.AttendanceRepository;
import com.studentautomation.repository.StudentRepository;
import com.studentautomation.repository.TeacherRepository;
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
 * preventing duplicate attendance, bulk attendance marking,
 * and fetching attendance records based on logged-in user roles.
 *
 * @author Yashvanth
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    /**
     * Constructor for injecting required repositories.
     *
     * Purpose:
     * Spring uses this constructor to provide repository objects
     * required for attendance business logic.
     *
     * @param attendanceRepository repository for attendance database operations
     * @param studentRepository repository for student database operations
     * @param teacherRepository repository for teacher database operations
     */
    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 StudentRepository studentRepository,
                                 TeacherRepository teacherRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * Marks attendance for a single student by the logged-in teacher.
     *
     * Purpose:
     * This method validates the teacher, validates the student,
     * checks duplicate attendance, saves the record, and returns
     * a clean response DTO.
     *
     * @param request attendance request data
     * @param teacherEmail email of the logged-in teacher
     * @return saved attendance response
     */
    @Override
    @Transactional
    public AttendanceResponseDTO markAttendance(AttendanceRequestDTO request, String teacherEmail) {

        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail)
                .orElseThrow(() -> new InvalidRequestException("Teacher profile not found"));

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new InvalidRequestException("Student not found"));

        boolean alreadyExists = attendanceRepository
                .existsByStudentIdAndAttendanceDateAndSubjectNameAndPeriodNumber(
                        request.studentId(),
                        request.attendanceDate(),
                        request.subjectName().trim(),
                        request.periodNumber()
                );

        if (alreadyExists) {
            throw new DuplicateResourceException(
                    "Attendance already marked for this student, date, subject, and period"
            );
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setMarkedBy(teacher);
        attendance.setAttendanceDate(request.attendanceDate());
        attendance.setSubjectName(request.subjectName().trim());
        attendance.setPeriodNumber(request.periodNumber());
        attendance.setStatus(request.status());
        attendance.setRemarks(cleanRemarks(request.remarks()));

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return mapToResponse(savedAttendance);
    }

    /**
     * Marks attendance for multiple students at once.
     *
     * Purpose:
     * This method allows a teacher to mark attendance for an entire class
     * using one API request instead of marking one student at a time.
     *
     * Business Rule:
     * If any one record has an error, no attendance record will be saved.
     *
     * Validation:
     * 1. Teacher must exist.
     * 2. Student must exist.
     * 3. Same student should not be repeated inside the same request.
     * 4. Student profile must be active.
     * 5. Attendance should not already exist for same student, date, subject, and period.
     *
     * @param request bulk attendance request data
     * @param teacherEmail email of the logged-in teacher
     * @return bulk attendance result with success count and row-wise errors
     */
    @Override
    @Transactional
    public BulkAttendanceResponseDTO markBulkAttendance(
            BulkAttendanceRequestDTO request,
            String teacherEmail
    ) {
        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail)
                .orElseThrow(() -> new InvalidRequestException("Teacher profile not found"));

        List<BulkAttendanceErrorDTO> errors = new ArrayList<>();
        List<Attendance> attendanceListToSave = new ArrayList<>();

        Set<Long> studentIdsInRequest = new HashSet<>();

        String subjectName = request.subjectName().trim();

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

            /*
             * Purpose:
             * This validation prevents attendance marking for inactive students.
             *
             * Important:
             * Boolean.TRUE.equals(...) is used because it safely handles null values also.
             */
            if (!Boolean.TRUE.equals(student.getActive())) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "studentId",
                        "Student profile is inactive"
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

            boolean alreadyExists = attendanceRepository
                    .existsByStudentIdAndAttendanceDateAndSubjectNameAndPeriodNumber(
                            studentId,
                            request.attendanceDate(),
                            subjectName,
                            request.periodNumber()
                    );

            if (alreadyExists) {
                errors.add(new BulkAttendanceErrorDTO(
                        studentId,
                        "attendance",
                        "Attendance already marked for this student, date, subject, and period"
                ));
                continue;
            }

            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setMarkedBy(teacher);
            attendance.setAttendanceDate(request.attendanceDate());
            attendance.setSubjectName(subjectName);
            attendance.setPeriodNumber(request.periodNumber());
            attendance.setStatus(record.status());
            attendance.setRemarks(cleanRemarks(record.remarks()));

            attendanceListToSave.add(attendance);
        }

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
     * Gets attendance records marked by the logged-in teacher for a date.
     *
     * Purpose:
     * This method is used by teachers to view attendance records
     * they marked on a specific date.
     *
     * @param attendanceDate date of attendance
     * @param teacherEmail email of the logged-in teacher
     * @return list of attendance records
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByDateForTeacher(
            LocalDate attendanceDate,
            String teacherEmail
    ) {
        Teacher teacher = teacherRepository.findByUser_Email(teacherEmail)
                .orElseThrow(() -> new InvalidRequestException("Teacher profile not found"));

        return attendanceRepository.findByMarkedByIdAndAttendanceDate(teacher.getId(), attendanceDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Gets attendance records of the logged-in student.
     *
     * Purpose:
     * This method is used by students to view only their own attendance.
     *
     * @param studentEmail email of the logged-in student
     * @return list of student's attendance records
     */
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getMyAttendance(String studentEmail) {

        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new InvalidRequestException("Student profile not found"));

        return attendanceRepository.findByStudentId(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Converts Attendance entity to AttendanceResponseDTO.
     *
     * Purpose:
     * This method prevents exposing full entity objects directly to frontend.
     *
     * @param attendance attendance entity
     * @return attendance response DTO
     */
    private AttendanceResponseDTO mapToResponse(Attendance attendance) {

        Student student = attendance.getStudent();
        Teacher teacher = attendance.getMarkedBy();

        return new AttendanceResponseDTO(
                attendance.getId(),
                student.getId(),
                student.getName(),
                teacher.getId(),
                teacher.getName(),
                attendance.getAttendanceDate(),
                attendance.getSubjectName(),
                attendance.getPeriodNumber(),
                attendance.getStatus(),
                attendance.getRemarks(),
                attendance.getCreatedAt(),
                attendance.getUpdatedAt()
        );
    }

    /**
     * Cleans optional remarks before saving.
     *
     * Purpose:
     * This method trims remarks and converts empty remarks to null.
     *
     * @param remarks optional remarks
     * @return cleaned remarks or null
     */
    private String cleanRemarks(String remarks) {

        if (remarks == null || remarks.trim().isEmpty()) {
            return null;
        }

        return remarks.trim();
    }
}