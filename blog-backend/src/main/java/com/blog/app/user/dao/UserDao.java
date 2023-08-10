package com.blog.app.user.dao;

import com.blog.app.user.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findUserByEmail(String email);
    boolean saveUser(User user);
}
