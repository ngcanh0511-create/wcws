package com.wcpl.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, String>> handleApp(AppException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of("code", ex.getCode(), "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (a, b) -> a));
        return ResponseEntity.badRequest()
                .body(Map.of("code", "VALIDATION_ERROR", "errors", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(Map.of("code", "INTERNAL_ERROR", "message", "Đã xảy ra lỗi hệ thống"));
    }
}
