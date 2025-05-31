package com.hsbc.transaction.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * A custom exception class for handling business-specific errors in the application.
 * This exception extends RuntimeException and includes error codes and messages
 * for better error handling and reporting.
 *
 * <p>This implementation follows the Alibaba Java Development Manual specifications:
 * <ul>
 *     <li>Custom business exceptions extend RuntimeException</li>
 *     <li>Include error code and error message</li>
 *     <li>Support passing underlying exception cause</li>
 *     <li>Provide quick creation methods for business exceptions</li>
 * </ul>
 * </p>
 *
 * @author HSBC Development Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * The error code associated with this exception.
     */
    private final ErrorCode errorCode;

    /**
     * Additional data associated with this exception.
     */
    private final Object data;

    /**
     * Constructs a new business exception with the specified error code.
     *
     * @param errorCode the error code enum that represents the type of error
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = null;
    }

    /**
     * Constructs a new business exception with the specified error code and message.
     *
     * @param errorCode the error code enum that represents the type of error
     * @param message the detailed error message
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.data = null;
    }

    /**
     * Constructs a new business exception with the specified error code and data.
     *
     * @param errorCode the error code enum that represents the type of error
     * @param data additional data associated with the error
     */
    public BusinessException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }

    /**
     * Constructs a new business exception with the specified error code, message, and data.
     *
     * @param errorCode the error code enum that represents the type of error
     * @param message the detailed error message
     * @param data additional data associated with the error
     */
    public BusinessException(ErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return the HTTP status code that should be returned for this error
     */
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    /**
     * Creates a new business exception with the specified error code.
     * This is a convenience method for creating exceptions with default messages.
     *
     * @param errorCode the error code enum that represents the type of error
     * @return a new BusinessException instance
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    /**
     * Creates a new business exception with the specified error code and message.
     * This is a convenience method for creating exceptions with custom messages.
     *
     * @param errorCode the error code enum that represents the type of error
     * @param message the detailed error message
     * @return a new BusinessException instance
     */
    public static BusinessException of(ErrorCode errorCode, String message) {
        return new BusinessException(errorCode, message);
    }

    /**
     * Creates a new business exception with the specified error code and data.
     * This is a convenience method for creating exceptions with additional data.
     *
     * @param errorCode the error code enum that represents the type of error
     * @param data additional data associated with the error
     * @return a new BusinessException instance
     */
    public static BusinessException withData(ErrorCode errorCode, Object data) {
        return new BusinessException(errorCode, data);
    }

    /**
     * Returns a string representation of this exception.
     * The string includes the error code, message, HTTP status, and any additional data.
     *
     * @return a string representation of this exception
     */
    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + errorCode.getCode() +
                ", message='" + getMessage() + '\'' +
                ", httpStatus=" + getHttpStatus() +
                (data != null ? ", data=" + data : "") +
                '}';
    }
}
