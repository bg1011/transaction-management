package com.hsbc.transaction.aspect;

import com.hsbc.transaction.common.exception.BusinessException;
import com.hsbc.transaction.common.exception.IdempotencyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingRequestHeaderException;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling and consistent error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles idempotency exceptions.
     * Returns a 409 Conflict status code.
     */
    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<String> handleIdempotencyException(IdempotencyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Handles business exceptions.
     * Returns the status code specified in the exception.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }

    /**
     * Handles constraint violation exceptions.
     * Returns a 400 Bad Request status code.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body("Parameter validation failed: " + ex.getMessage());
    }

    /**
     * Handles method argument validation exceptions.
     * Returns a 400 Bad Request status code with field-specific error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles illegal argument exceptions.
     * Returns a 400 Bad Request status code.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Invalid parameter: " + ex.getMessage());
    }

    /**
     * Handles all other exceptions.
     * Returns a 500 Internal Server Error status code.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity.internalServerError().body("System error: " + ex.getMessage());
    }

    /**
     * Handles missing request header exceptions.
     * Returns a 400 Bad Request status code with header-specific error message.
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ex.getHeaderName(), "Missing required header: " + ex.getHeaderName());
        return ResponseEntity.badRequest().body(error);
    }
}
