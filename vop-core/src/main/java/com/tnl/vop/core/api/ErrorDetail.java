package com.tnl.vop.core.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Minimal ProblemDetails-like error payload (HTTP status is set by web layer). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorDetail {
    private final String code;                 // e.g. "VALIDATION_ERROR", "NOT_FOUND"
    private final String message;              // human-friendly
    private final Map<String, Object> details; // field errors, context, etc.

    private ErrorDetail(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    public static ErrorDetail of(String code, String message) {
        return new ErrorDetail(code, message, null);
    }

    public static ErrorDetail of(String code, String message, Map<String, Object> details) {
        return new ErrorDetail(code, message, details);
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Map<String, Object> getDetails() { return details; }
}
