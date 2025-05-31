package com.hsbc.transaction.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Standard error codes for the Bank Transaction System.
 * This enum defines the error codes, messages, and corresponding HTTP status codes
 * for various error scenarios in the application.
 *
 * <p>Error code ranges:
 * <ul>
 *     <li>0: Success</li>
 *     <li>1000-1999: Parameter validation errors</li>
 *     <li>2000-2999: Transaction related errors</li>
 *     <li>3000-3999: Idempotency related errors</li>
 *     <li>5000-5999: System errors</li>
 * </ul>
 * </p>
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
 * }
 * </pre>
 * </p>
 *
 * <p>Note: When adding new error codes, please follow the existing pattern and
 * maintain the error code ranges for different types of errors.</p>
 *
 * @author HSBC Development Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public enum ErrorCode {
    /**
     * Indicates a successful operation.
     * HTTP Status: 200 OK
     */
    SUCCESS(0, "success", HttpStatus.OK),

    /**
     * Indicates that the request parameters failed validation.
     * HTTP Status: 400 Bad Request
     */
    PARAM_VALIDATION_FAILED(1001, "Parameter validation failed", HttpStatus.BAD_REQUEST),

    /**
     * Indicates that the requested transaction was not found.
     * HTTP Status: 404 Not Found
     */
    TRANSACTION_NOT_FOUND(2001, "Transaction not found", HttpStatus.NOT_FOUND),

    /**
     * Indicates that the Idempotency-Key header is missing in the request.
     * HTTP Status: 400 Bad Request
     */
    IDEMPOTENCY_KEY_REQUIRED(3001, "Idempotency-Key header is required", HttpStatus.BAD_REQUEST),

    /**
     * Indicates that the request is a duplicate of a previously processed request.
     * HTTP Status: 409 Conflict
     */
    REPEATED_REQUEST(3002, "Repeated request", HttpStatus.CONFLICT),

    /**
     * Indicates an unexpected internal server error.
     * HTTP Status: 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(5000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    /**
     * The numeric code that uniquely identifies this error.
     */
    private final int code;

    /**
     * A human-readable message describing the error.
     */
    private final String message;

    /**
     * The HTTP status code that should be returned for this error.
     */
    private final HttpStatus httpStatus;

    /**
     * Constructs a new error code with the specified code, message, and HTTP status.
     *
     * @param code the numeric code that uniquely identifies this error
     * @param message a human-readable message describing the error
     * @param httpStatus the HTTP status code that should be returned for this error
     */
    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
