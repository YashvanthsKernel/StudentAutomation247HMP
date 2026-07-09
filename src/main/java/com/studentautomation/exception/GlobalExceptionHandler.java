package com.studentautomation.exception;

import com.studentautomation.dto.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the complete application.
 *
 * Purpose:
 * This class catches errors from controller/service layer
 * and sends clean API responses instead of confusing error pages.
 *
 * @author Yashvanth
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from DTO classes.
     *
     * Purpose:
     * This catches errors from annotations like @NotBlank, @Email,
     * @Size, @Pattern, @Min, and @Max.
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
     * Handles duplicate resource errors.
     *
     * Example:
     * Email already exists.
     * Register number already exists.
     * Employee ID already exists.
     *
     * @param exception duplicate resource exception object
     * @return conflict error response
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(
            DuplicateResourceException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(exception.getMessage()));
    }

    /**
     * Handles not-found errors.
     *
     * Example:
     * Student profile not found.
     * Teacher profile not found.
     * User not found.
     *
     * @param exception resource not found exception object
     * @return not found error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(exception.getMessage()));
    }

    /**
     * Handles invalid request errors.
     *
     * Example:
     * Password and confirm password do not match.
     * Student profile is inactive.
     *
     * @param exception invalid request exception object
     * @return bad request error response
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidRequestException(
            InvalidRequestException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(exception.getMessage()));
    }

    /**
     * Handles wrong JSON body format.
     *
     * Example:
     * Missing comma.
     * Wrong JSON syntax.
     * Invalid request body format.
     *
     * @param exception JSON parse exception object
     * @return bad request error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("Invalid JSON request body"));
    }

    /**
     * Handles wrong HTTP method errors.
     *
     * Example:
     * Calling GET API using POST.
     * Calling POST API using GET.
     *
     * @param exception method not supported exception object
     * @return method not allowed error response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception) {

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.failure("HTTP method not allowed for this API"));
    }

    /**
     * Handles database constraint errors.
     *
     * Purpose:
     * This acts as a safety net if duplicate data reaches database level.
     *
     * @param exception database integrity exception object
     * @return conflict error response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure("Duplicate or invalid database value"));
    }

    /**
     * Handles bad login credential errors.
     *
     * @param exception bad credentials exception object
     * @return unauthorized error response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
            BadCredentialsException exception) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure("Invalid email or password"));
    }

    /**
     * Handles user not found errors from Spring Security.
     *
     * @param exception username not found exception object
     * @return unauthorized error response
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(
            UsernameNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(exception.getMessage()));
    }

    /**
     * Handles access denied errors.
     *
     * Purpose:
     * This is a backup handler. Main 403 handling will be done in SecurityConfig.
     *
     * @param exception access denied exception object
     * @return forbidden error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure("You do not have permission to access this API"));
    }

    /**
     * Handles old RuntimeException errors.
     *
     * Purpose:
     * Keep this temporarily because some old service code may still use
     * throw new RuntimeException(...).
     *
     * @param exception runtime exception object
     * @return bad request error response
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

        String exceptionName = exception.getClass().getSimpleName();

        if ("NoResourceFoundException".equals(exceptionName)
                || "NoHandlerFoundException".equals(exceptionName)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("API endpoint not found"));
        }

        exception.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Something went wrong. Please try again later."));
    }
}