package com.studentautomation.exception;

import com.studentautomation.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the complete application.
 *
 * Purpose:
 * This class catches errors from the controller/service layer
 * and sends a clean API response instead of ugly error pages.
 *
 * @author Yashvanth
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from DTO classes.
     *
     * Example:
     * name is blank,
     * email is invalid,
     * phone number is invalid.
     *
     * @param exception validation exception object
     * @return clean validation error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", errors));
    }

    /**
     * Handles runtime errors thrown manually from service layer.
     *
     * Example:
     * throw new RuntimeException("Student not found");
     * throw new RuntimeException("Email already exists");
     *
     * @param exception runtime exception object
     * @return clean error response
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(exception.getMessage()));
    }

    /**
     * Handles unknown errors in the application.
     *
     * Purpose:
     * This prevents internal stack trace from going to frontend.
     *
     * @param exception general exception object
     * @return clean internal server error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception exception) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Something went wrong. Please try again later."));
    }
}