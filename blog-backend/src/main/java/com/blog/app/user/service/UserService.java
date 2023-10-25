package com.blog.app.user.service;

import com.blog.app.post.model.Post;
import com.blog.app.user.model.User;

import java.util.List;


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
