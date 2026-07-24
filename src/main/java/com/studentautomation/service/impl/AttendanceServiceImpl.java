package com.studentautomation.service.impl;

import com.studentautomation.dto.request.AttendanceRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRecordRequestDTO;
import com.studentautomation.dto.request.BulkAttendanceRequestDTO;
import com.studentautomation.dto.response.*;
import com.studentautomation.entity.*;
import com.studentautomation.enums.AttendanceStatus;
import com.studentautomation.exception.DuplicateResourceException;
import com.studentautomation.exception.InvalidRequestException;
import com.studentautomation.exception.ResourceNotFoundException;
import com.studentautomation.mapper.StudentMapper;
import com.studentautomation.repository.*;
import com.studentautomation.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final AcademicClassRepository academicClassRepository;

    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            SubjectRepository subjectRepository,
            TeacherSubjectRepository teacherSubjectRepository,
            StudentSubjectRepository studentSubjectRepository,
            AcademicClassRepository academicClassRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.teacherSubjectRepository = teacherSubjectRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.academicClassRepository = academicClassRepository;
    }

    @Override
    @Transactional
    public AttendanceResponseDTO markAttendance(AttendanceRequestDTO request, String teacherEmail) {
        if (request.attendanceDate() != null && request.attendanceDate().isAfter(LocalDate.now())) {
            throw new InvalidRequestException("Cannot mark attendance for a future date");
        }

        Teacher teacher = getActiveTeacher(teacherEmail);
        Subject subject = getActiveSubject(request.subjectId());
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        validateActiveStudent(student);

        if (attendanceRepository.existsByStudent_IdAndSubject_IdAndAttendanceDateAndPeriodNumber(
                student.getId(), subject.getId(), request.attendanceDate(), request.periodNumber()
        )) {
            throw new DuplicateResourceException("Attendance already marked for this student, subject, date and period");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setMarkedBy(teacher);
        attendance.setAttendanceDate(request.attendanceDate());
        attendance.setPeriodNumber(request.periodNumber());
        attendance.setStatus(request.status());
        attendance.setRemarks(request.remarks());

        return mapToDTO(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByDateForTeacher(LocalDate attendanceDate, String teacherEmail) {
        Teacher teacher = getActiveTeacher(teacherEmail);
        return attendanceRepository.findByMarkedBy_IdAndAttendanceDate(teacher.getId(), attendanceDate)
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceWithFilters(LocalDate date, Long subjectId, String section, Integer periodNumber, String teacherEmail) {
        Teacher teacher = getActiveTeacher(teacherEmail);
        return attendanceRepository.findByMarkedBy_IdAndAttendanceDate(teacher.getId(), date != null ? date : LocalDate.now())
                .stream()
                .filter(a -> subjectId == null || a.getSubject().getId().equals(subjectId))
                .filter(a -> section == null || (a.getStudent().getSection() != null && a.getStudent().getSection().equalsIgnoreCase(section)))
                .filter(a -> periodNumber == null || a.getPeriodNumber().equals(periodNumber))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAttendanceRoster(Long subjectId, String section, String academicYear, String teacherEmail) {
        Subject subject = getActiveSubject(subjectId);
        List<Student> students = studentRepository.findByDepartmentAndSemester(subject.getDepartment(), subject.getSemester())
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getActive()))
                .filter(s -> section == null || (s.getSection() != null && s.getSection().equalsIgnoreCase(section)))
                .filter(s -> academicYear == null || (s.getAcademicYear() != null && s.getAcademicYear().equalsIgnoreCase(academicYear)))
                .toList();

        return students.stream().map(StudentMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponseDTO getAttendanceById(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
        return mapToDTO(attendance);
    }

    @Override
    @Transactional
    public AttendanceResponseDTO updateAttendance(Long attendanceId, AttendanceRequestDTO request, String teacherEmail) {
        if (request.attendanceDate() != null && request.attendanceDate().isAfter(LocalDate.now())) {
            throw new InvalidRequestException("Cannot set attendance for a future date");
        }

        Teacher teacher = getActiveTeacher(teacherEmail);
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        if (attendance.getMarkedBy() != null && !attendance.getMarkedBy().getId().equals(teacher.getId())) {
            throw new InvalidRequestException("You are not authorized to modify attendance marked by another teacher");
        }

        attendance.setStatus(request.status());
        attendance.setRemarks(request.remarks());
        return mapToDTO(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public AttendanceResponseDTO updateAttendanceStatus(Long attendanceId, String status, String teacherEmail) {
        Teacher teacher = getActiveTeacher(teacherEmail);
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        if (attendance.getMarkedBy() != null && !attendance.getMarkedBy().getId().equals(teacher.getId())) {
            throw new InvalidRequestException("You are not authorized to modify attendance marked by another teacher");
        }

        AttendanceStatus newStatus = AttendanceStatus.valueOf(status.trim().toUpperCase());
        attendance.setStatus(newStatus);
        return mapToDTO(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public void deleteAttendance(Long attendanceId, String teacherEmail) {
        Teacher teacher = getActiveTeacher(teacherEmail);
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        if (attendance.getMarkedBy() != null && !attendance.getMarkedBy().getId().equals(teacher.getId())) {
            throw new InvalidRequestException("You are not authorized to delete attendance marked by another teacher");
        }

        attendanceRepository.delete(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getMyAttendance(String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return attendanceRepository.findByStudent_Id(student.getId()).stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getMyAttendanceFiltered(Long subjectId, LocalDate fromDate, LocalDate toDate, String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        return attendanceRepository.findByStudent_Id(student.getId())
                .stream()
                .filter(a -> subjectId == null || a.getSubject().getId().equals(subjectId))
                .filter(a -> fromDate == null || !a.getAttendanceDate().isBefore(fromDate))
                .filter(a -> toDate == null || !a.getAttendanceDate().isAfter(toDate))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentAttendanceSummaryDTO getMyAttendanceSummary(String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        List<Attendance> records = attendanceRepository.findByStudent_Id(student.getId());
        return calculateSummary(student, null, records);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentAttendanceSummaryDTO getMySubjectAttendanceSummary(Long subjectId, String studentEmail) {
        Student student = studentRepository.findByUser_Email(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Subject subject = getActiveSubject(subjectId);
        List<Attendance> records = attendanceRepository.findByStudent_Id(student.getId())
                .stream()
                .filter(a -> a.getSubject().getId().equals(subjectId))
                .toList();

        return calculateSummary(student, subject, records);
    }

    @Override
    @Transactional
    public BulkAttendanceResponseDTO markBulkAttendance(BulkAttendanceRequestDTO request, String teacherEmail) {
        if (request.attendanceDate() != null && request.attendanceDate().isAfter(LocalDate.now())) {
            throw new InvalidRequestException("Cannot mark attendance for a future date");
        }

        Teacher teacher = getActiveTeacher(teacherEmail);
        Subject subject = getActiveSubject(request.subjectId());

        List<AttendanceResponseDTO> successfulRecords = new ArrayList<>();
        List<BulkAttendanceErrorDTO> errors = new ArrayList<>();

        for (BulkAttendanceRecordRequestDTO record : request.records()) {
            try {
                AttendanceRequestDTO dto = new AttendanceRequestDTO(
                        record.studentId(),
                        request.attendanceDate(),
                        subject.getId(),
                        request.periodNumber(),
                        record.status(),
                        record.remarks()
                );

                successfulRecords.add(markAttendance(dto, teacherEmail));
            } catch (Exception e) {
                errors.add(new BulkAttendanceErrorDTO(record.studentId(), "attendanceStatus", e.getMessage()));
            }
        }

        return new BulkAttendanceResponseDTO(
                request.records().size(),
                successfulRecords.size(),
                errors.size(),
                errors
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByStudentId(Long studentId) {
        return attendanceRepository.findByStudent_Id(studentId).stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByClassId(Long classId) {
        AcademicClass ac = academicClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        List<Student> students = studentRepository.findByDepartmentAndSemester(ac.getDepartmentCode(), ac.getSemester())
                .stream()
                .filter(s -> s.getSection() != null && s.getSection().equalsIgnoreCase(ac.getSection()))
                .toList();

        List<AttendanceResponseDTO> list = new ArrayList<>();
        for (Student s : students) {
            list.addAll(attendanceRepository.findByStudent_Id(s.getId()).stream().map(this::mapToDTO).toList());
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceBySubjectId(Long subjectId) {
        return attendanceRepository.findAll()
                .stream()
                .filter(a -> a.getSubject().getId().equals(subjectId))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentAttendanceSummaryDTO> getAttendanceShortage() {
        List<Student> allStudents = studentRepository.findAll();
        List<StudentAttendanceSummaryDTO> shortageList = new ArrayList<>();

        for (Student s : allStudents) {
            List<Attendance> records = attendanceRepository.findByStudent_Id(s.getId());
            if (!records.isEmpty()) {
                StudentAttendanceSummaryDTO summary = calculateSummary(s, null, records);
                if (summary.attendancePercentage() < 75.0) {
                    shortageList.add(summary);
                }
            }
        }
        return shortageList;
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceReportDTO getAttendanceReport() {
        List<Attendance> all = attendanceRepository.findAll();
        if (all.isEmpty()) {
            return new AttendanceReportDTO(0, 0, 0, 0, 0, 0.0, 0);
        }

        int p = 0, a = 0, l = 0, e = 0;
        for (Attendance att : all) {
            if (att.getStatus() == AttendanceStatus.PRESENT) p++;
            else if (att.getStatus() == AttendanceStatus.ABSENT) a++;
            else if (att.getStatus() == AttendanceStatus.LATE) l++;
            else if (att.getStatus() == AttendanceStatus.EXCUSED) e++;
        }

        double pct = (double) p / all.size() * 100.0;
        int shortageCount = getAttendanceShortage().size();

        return new AttendanceReportDTO(
                all.size(), p, a, l, e,
                Math.round(pct * 100.0) / 100.0,
                shortageCount
        );
    }

    private StudentAttendanceSummaryDTO calculateSummary(Student student, Subject subject, List<Attendance> records) {
        int total = records.size();
        int present = 0, absent = 0, late = 0, excused = 0;

        for (Attendance r : records) {
            if (r.getStatus() == AttendanceStatus.PRESENT) present++;
            else if (r.getStatus() == AttendanceStatus.ABSENT) absent++;
            else if (r.getStatus() == AttendanceStatus.LATE) late++;
            else if (r.getStatus() == AttendanceStatus.EXCUSED) excused++;
        }

        double pct = total > 0 ? ((double) present / total) * 100.0 : 0.0;

        int classesNeeded = 0;
        if (pct < 75.0 && total > 0) {
            double needed = (0.75 * total - present) / 0.25;
            classesNeeded = (int) Math.ceil(needed);
        }

        return new StudentAttendanceSummaryDTO(
                student.getId(),
                student.getRegNo(),
                student.getName(),
                subject != null ? subject.getId() : null,
                subject != null ? subject.getSubjectCode() : null,
                subject != null ? subject.getSubjectName() : null,
                total,
                present,
                absent,
                late,
                excused,
                Math.round(pct * 100.0) / 100.0,
                classesNeeded
        );
    }

    private Teacher getActiveTeacher(String email) {
        Teacher teacher = teacherRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));
        if (Boolean.FALSE.equals(teacher.getActive())) {
            throw new InvalidRequestException("Teacher account is inactive");
        }
        return teacher;
    }

    private Subject getActiveSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        if (Boolean.FALSE.equals(subject.getActive())) {
            throw new InvalidRequestException("Subject is inactive");
        }
        return subject;
    }

    private void validateActiveStudent(Student student) {
        if (Boolean.FALSE.equals(student.getActive())) {
            throw new InvalidRequestException("Student profile is inactive");
        }
    }

    private AttendanceResponseDTO mapToDTO(Attendance a) {
        return new AttendanceResponseDTO(
                a.getId(),
                a.getStudent().getId(),
                a.getStudent().getName(),
                a.getMarkedBy().getId(),
                a.getMarkedBy().getName(),
                a.getSubject().getId(),
                a.getSubject().getSubjectCode(),
                a.getSubject().getSubjectName(),
                a.getAttendanceDate(),
                a.getPeriodNumber(),
                a.getStatus(),
                a.getRemarks(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}