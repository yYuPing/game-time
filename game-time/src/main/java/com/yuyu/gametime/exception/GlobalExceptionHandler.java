package com.yuyu.gametime.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<com.yuyu.gametime.dto.ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fieldError -> fieldError.getField(),
                fieldError -> fieldError.getDefaultMessage(),
                (existing, replacement) -> existing
            ));
        return ResponseEntity.badRequest().body(com.yuyu.gametime.dto.ApiResponse.fail(errors.toString(), 400));
        }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.yuyu.gametime.dto.ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(com.yuyu.gametime.dto.ApiResponse.fail(ex.getMessage(), 400));
    }
}    
