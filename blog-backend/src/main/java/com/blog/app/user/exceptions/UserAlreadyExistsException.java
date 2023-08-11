package com.blog.app.user.exceptions;

import com.blog.app.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class UserAlreadyExistsException extends CustomException {
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.NOT_FOUND);
        log.error(message);
    }

}
