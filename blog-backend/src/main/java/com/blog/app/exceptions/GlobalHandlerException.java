package com.blog.app.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(CustomException.class)
    ResponseEntity<?> handleError(CustomException ex) {
        log.warn("Error: {}", ex.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put("error", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(map);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleError(Exception ex) {
        log.warn("Error: {}", ex.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put("error", ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(map);
    }


}
