package com.blog.app.user.service;

import com.blog.app.user.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(Long id);
    boolean editUser(User user);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    // todo: favorite posts
}
