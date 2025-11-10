package com.tnl.vop.platform.web;

import com.tnl.vop.core.api.ApiResponse;
import com.tnl.vop.core.api.ErrorDetail;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return toResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", msg, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining("; "));
        return toResponse(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", msg, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex) {
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR", ex.getMessage(), ex);
    }

    private ResponseEntity<ApiResponse<Void>> toResponse(HttpStatus status, String code, String message, Exception ex) {
        // Log full stack trace; keep response concise
        log.error("Request failed [{}]: {}", code, message, ex);

        // Use factory, not constructor
        ErrorDetail err = ErrorDetail.of(code, message, null);

        // Rely on ApiResponse.error(...) without chaining a timestamp
        ApiResponse<Void> body = ApiResponse.error(err);

        return ResponseEntity.status(status).body(body);
    }
}
