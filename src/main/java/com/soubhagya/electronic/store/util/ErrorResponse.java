package com.soubhagya.electronic.store.util;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The ErrorResponse class is a data structure used for representing error responses in an application.
 * It contains information about the error, such as the timestamp when the error occurred,
 * a specific error code, a descriptive message, and any additional field-specific errors.
 */
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String errorCode;
    private String message;
    private Map<String, String> fieldErrors;

    public ErrorResponse(String errorCode, String message, Map<String, String> fieldErrors) {
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    // Getters and setters

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
