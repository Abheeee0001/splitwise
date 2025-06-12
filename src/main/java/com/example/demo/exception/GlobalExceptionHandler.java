package com.example.demo.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.model.enums.SplitType;
import com.example.demo.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, errorMsg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgs(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, "Unexpected error: " + ex.getMessage()));
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidFormat(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException formatEx) {
            String fieldName = formatEx.getPath().stream()
                .map(ref -> ref.getFieldName())
                .reduce((f1, f2) -> f1 + "." + f2)
                .orElse("unknown");

            String msg = "Invalid value for field '" + fieldName + "'. Expected one of: " +
                         java.util.Arrays.toString(SplitType.values());

            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, msg));
        }

        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid request payload"));
    }
}
