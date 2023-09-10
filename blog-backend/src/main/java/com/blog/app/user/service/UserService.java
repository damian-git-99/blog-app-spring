package com.blog.app.user.service;

import com.blog.app.user.dto.UserInfoResponseDTO;
import com.blog.app.user.model.User;

import java.util.Optional;

public interface UserService {
    Optional<UserInfoResponseDTO> findUserById(Long id);
    Optional<UserInfoResponseDTO> getAuthenticatedUserInfo();
    boolean editUser(User user);
    Optional<UserInfoResponseDTO> findUserByUsername(String username);
    Optional<UserInfoResponseDTO> findUserByEmail(String email);
    void addFavoritePost(Long postId);
    void removeFavoritePost(Long postId);
    boolean isPostMarkedAsFavorite(Long postId);
}
