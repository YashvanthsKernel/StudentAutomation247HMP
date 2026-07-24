package com.studentautomation.controller;

import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.TimetableSlotResponseDTO;
import com.studentautomation.service.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/timetable")
@PreAuthorize("hasRole('STUDENT')")
public class StudentTimetableController {

    private final TimetableService timetableService;

    public StudentTimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<TimetableSlotResponseDTO>>> getMyTimetable(
            Authentication authentication
    ) {
        List<TimetableSlotResponseDTO> response = timetableService.getStudentTimetable(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student timetable fetched successfully", response));
    }

    @GetMapping("/me/today")
    public ResponseEntity<ApiResponse<List<TimetableSlotResponseDTO>>> getMyTimetableToday(
            Authentication authentication
    ) {
        List<TimetableSlotResponseDTO> response = timetableService.getStudentTimetableToday(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student today's timetable fetched successfully", response));
    }
}
