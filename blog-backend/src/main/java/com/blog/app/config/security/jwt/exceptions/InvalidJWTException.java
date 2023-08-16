package com.blog.app.config.security.jwt.exceptions;

import com.blog.app.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class InvalidJWTException extends CustomException {
    public InvalidJWTException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
        log.error("JWT validation failed: {}", message);
    }
}
