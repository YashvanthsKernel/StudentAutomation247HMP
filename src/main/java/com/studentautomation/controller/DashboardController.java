package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.DashboardSummaryDTO;
import com.studentautomation.entity.*;
import com.studentautomation.repository.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final AcademicClassRepository academicClassRepository;
    private final ExamRepository examRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;

    public DashboardController(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            SubjectRepository subjectRepository,
            DepartmentRepository departmentRepository,
            AcademicClassRepository academicClassRepository,
            ExamRepository examRepository,
            AttendanceRepository attendanceRepository,
            MarkRepository markRepository
    ) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.academicClassRepository = academicClassRepository;
        this.examRepository = examRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
    }

    @GetMapping("/api/admin/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard summary fetched successfully", calculateSummary()));
    }

    @GetMapping("/api/super-admin/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getSuperAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Super Admin dashboard summary fetched successfully", calculateSummary()));
    }

    @GetMapping("/api/teacher/dashboard")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getTeacherDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Teacher dashboard summary fetched successfully", calculateSummary()));
    }

    @GetMapping("/api/student/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getStudentDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Student dashboard summary fetched successfully", calculateSummary()));
    }

    private DashboardSummaryDTO calculateSummary() {
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalSubjects = subjectRepository.count();
        long totalDepartments = departmentRepository.count();
        long totalClasses = academicClassRepository.count();
        long totalExams = examRepository.count();

        List<Attendance> allAtt = attendanceRepository.findAll();
        long totalAtt = allAtt.size();
        long presentAtt = allAtt.stream().filter(a -> a.getStatus() == com.studentautomation.enums.AttendanceStatus.PRESENT).count();
        double attRate = totalAtt > 0 ? Math.round((presentAtt * 100.0 / totalAtt) * 10.0) / 10.0 : 0.0;

        List<Mark> allMarks = markRepository.findAll();
        long totalMarks = allMarks.size();
        long passMarks = allMarks.stream().filter(m -> Boolean.TRUE.equals(m.getIsPass())).count();
        double passRate = totalMarks > 0 ? Math.round((passMarks * 100.0 / totalMarks) * 10.0) / 10.0 : 0.0;

        return new DashboardSummaryDTO(
                totalStudents, totalTeachers, totalSubjects, totalDepartments,
                totalClasses, totalExams, attRate, passRate
        );
    }
}
