package com.tnl.vop.platform.web;

import com.tnl.vop.core.api.ApiResponse;
import com.tnl.vop.core.api.ErrorCode;
import com.tnl.vop.core.api.ErrorDetail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global, framework-level exception mapper.
 *
 * - Converts validation exceptions into a stable error payload with code + field details.
 * - Hides internal exception messages for 500 responses
 * - Logs full stack traces server-side.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- 400 – Validation errors ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // Collect field -> message for all validation errors on request body
        Map<String, String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                (existing, replacement) -> existing,      // keep first
                LinkedHashMap::new));

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("fieldErrors", fieldErrors);

        ErrorDetail error = ErrorDetail.of(
            ErrorCode.VALIDATION_ERROR,
            "Request validation failed.",
            details
        );

        return toResponse(HttpStatus.BAD_REQUEST, error, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> violations = ex.getConstraintViolations()
            .stream()
            .collect(Collectors.toMap(
                v -> v.getPropertyPath() != null ? v.getPropertyPath().toString() : "",
                ConstraintViolation::getMessage,
                (existing, replacement) -> existing,
                LinkedHashMap::new));

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("violations", violations);

        ErrorDetail error = ErrorDetail.of(
            ErrorCode.CONSTRAINT_VIOLATION,
            "Constraint violation.",
            details
        );

        return toResponse(HttpStatus.BAD_REQUEST, error, ex);
    }

    // ---------- 500 – Catch-all (no internal details) ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex) {
        // Do NOT leak ex.getMessage() to clients – keep it generic
        ErrorDetail error = ErrorDetail.of(
            ErrorCode.UNEXPECTED_ERROR,
            "An unexpected error occurred. Please contact support if this persists."
        );

        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, error, ex);
    }

    // ---------- Helper ----------

    private ResponseEntity<ApiResponse<Void>> toResponse(HttpStatus status, ErrorDetail error, Exception ex) {
        // Log full stack trace; keep response concise and safe
        log.error("Request failed [{}]: {}", error.getCode(), error.getMessage(), ex);

        ApiResponse<Void> body = ApiResponse.error(error);
        return ResponseEntity.status(status).body(body);
    }
}
