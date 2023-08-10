package com.blog.app.user.service;

import com.blog.app.user.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String email);
    void registerUser(User user);
}
