package com.blog.app.post.exceptions;

import com.blog.app.exceptions.CustomException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends CustomException {
    public PostNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
