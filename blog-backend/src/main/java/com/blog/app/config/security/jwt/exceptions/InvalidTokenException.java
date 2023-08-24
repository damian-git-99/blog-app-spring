package com.blog.app.config.security.jwt.exceptions;

import com.blog.app.exceptions.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
