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
@RequestMapping("/api/teacher/timetable")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherTimetableController {

    private final TimetableService timetableService;

    public TeacherTimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<TimetableSlotResponseDTO>>> getMyTimetable(
            Authentication authentication
    ) {
        List<TimetableSlotResponseDTO> response = timetableService.getTeacherTimetable(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Teacher timetable fetched successfully", response));
    }

    @GetMapping("/me/today")
    public ResponseEntity<ApiResponse<List<TimetableSlotResponseDTO>>> getMyTimetableToday(
            Authentication authentication
    ) {
        List<TimetableSlotResponseDTO> response = timetableService.getTeacherTimetableToday(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Teacher today's timetable fetched successfully", response));
    }
}
