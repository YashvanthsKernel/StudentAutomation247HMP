package com.studentautomation.exception;

/**
 * Exception class for not-found errors.
 *
 * Purpose:
 * This exception is thrown when requested data is not found,
 * such as student profile, teacher profile, or user account.
 *
 * @author Yashvanth
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates resource not found exception with message.
     *
     * @param message readable error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}