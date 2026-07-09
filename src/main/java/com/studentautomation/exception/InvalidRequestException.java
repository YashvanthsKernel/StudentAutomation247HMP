package com.studentautomation.exception;

/**
 * Exception class for invalid request errors.
 *
 * Purpose:
 * This exception is thrown when request data is logically wrong,
 * such as password mismatch or inactive profile.
 *
 * @author Yashvanth
 */
public class InvalidRequestException extends RuntimeException {

    /**
     * Creates invalid request exception with message.
     *
     * @param message readable error message
     */
    public InvalidRequestException(String message) {
        super(message);
    }
}