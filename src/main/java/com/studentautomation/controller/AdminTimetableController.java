package com.studentautomation.controller;

import com.studentautomation.dto.request.TimetableSlotRequestDTO;
import com.studentautomation.dto.response.ApiResponse;
import com.studentautomation.dto.response.TimetableSlotResponseDTO;
import com.studentautomation.service.TimetableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/timetable")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminTimetableController {

    private final TimetableService timetableService;

    public AdminTimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TimetableSlotResponseDTO>> createSlot(
            @Valid @RequestBody TimetableSlotRequestDTO request
    ) {
        TimetableSlotResponseDTO response = timetableService.createSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Timetable slot created successfully", response));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<TimetableSlotResponseDTO>>> createBulkSlots(
            @Valid @RequestBody List<TimetableSlotRequestDTO> requests
    ) {
        List<TimetableSlotResponseDTO> response = timetableService.createBulkSlots(requests);
        return ResponseEntity.ok(ApiResponse.success("Bulk timetable slots created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TimetableSlotResponseDTO>>> getAllSlots() {
        List<TimetableSlotResponseDTO> response = timetableService.getAllSlots();
        return ResponseEntity.ok(ApiResponse.success("Timetable slots fetched successfully", response));
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<ApiResponse<TimetableSlotResponseDTO>> getSlotById(
            @PathVariable Long slotId
    ) {
        TimetableSlotResponseDTO response = timetableService.getSlotById(slotId);
        return ResponseEntity.ok(ApiResponse.success("Timetable slot fetched successfully", response));
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<ApiResponse<TimetableSlotResponseDTO>> updateSlot(
            @PathVariable Long slotId,
            @Valid @RequestBody TimetableSlotRequestDTO request
    ) {
        TimetableSlotResponseDTO response = timetableService.updateSlot(slotId, request);
        return ResponseEntity.ok(ApiResponse.success("Timetable slot updated successfully", response));
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<ApiResponse<Object>> deleteSlot(
            @PathVariable Long slotId
    ) {
        timetableService.deleteSlot(slotId);
        return ResponseEntity.ok(ApiResponse.success("Timetable slot deleted successfully", null));
    }

    @GetMapping("/conflicts")
    public ResponseEntity<ApiResponse<List<String>>> getConflicts() {
        List<String> conflicts = timetableService.getConflicts();
        return ResponseEntity.ok(ApiResponse.success("Timetable conflicts checked successfully", conflicts));
    }
}
