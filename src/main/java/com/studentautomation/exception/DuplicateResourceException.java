package com.studentautomation.exception;

/**
 * Exception class for duplicate data errors.
 *
 * Purpose:
 * This exception is thrown when the user tries to create data
 * that already exists, such as duplicate email, register number,
 * or employee ID.
 *
 * @author Yashvanth
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Creates duplicate resource exception with message.
     *
     * @param message readable error message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}