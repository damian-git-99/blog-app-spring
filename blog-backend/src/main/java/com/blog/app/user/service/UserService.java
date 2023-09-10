package com.blog.app.user.service;

import com.blog.app.config.security.authentication.JWTAuthentication;
import com.blog.app.user.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(Long id);
    Optional<User> getAuthenticatedUserInfo();
    boolean editUser(User user);
    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    void addFavoritePost(Long postId);
    void removeFavoritePost(Long postId);
    boolean isPostMarkedAsFavorite(Long postId);
}
