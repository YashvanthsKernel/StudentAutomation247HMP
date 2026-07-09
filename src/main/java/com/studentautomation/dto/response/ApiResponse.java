package com.studentautomation.dto.response;

/**
 * Common API response structure used by all controllers.
 *
 * Purpose:
 * This record helps the backend send a standard response format
 * for both success and failure cases.
 *
 * Example response:
 * {
 *     "success": true,
 *     "message": "Student created successfully",
 *     "data": {}
 * }
 *
 * @param success tells whether the API request was successful or not
 * @param message readable message for frontend/user
 * @param data actual response data
 * @param <T> type of data returned in the response
 * @author Yashvanth
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {

    /**
     * Creates a success response.
     *
     * @param message success message
     * @param data actual response data
     * @param <T> type of response data
     * @return ApiResponse with success as true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates a failure response.
     *
     * @param message error message
     * @param <T> type of response data
     * @return ApiResponse with success as false and data as null
     */
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null);
    }
}