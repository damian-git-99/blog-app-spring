package com.blog.app.user.service;

import com.blog.app.user.model.User;


public interface UserService {
    User findUserById(Long id);
    User getAuthenticatedUserInfo();
    boolean editUser(User user);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    void addFavoritePost(Long postId);
    void removeFavoritePost(Long postId);
    boolean isPostMarkedAsFavorite(Long postId);
}
