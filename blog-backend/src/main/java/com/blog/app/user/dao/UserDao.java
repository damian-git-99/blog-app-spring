package com.blog.app.user.dao;

import com.blog.app.post.model.Post;
import com.blog.app.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByEmailOrUsername(String email, String username);
    boolean saveUser(User user);
    Optional<User> findUserById(Long id);
    boolean editUser(User user);
    Optional<User> findUserByUsername(String username);
    // todo: move favorites to separate package
    void addFavoritePost(Long postId, Long userId);
    void removeFavoritePost(Long postId, Long userId);
    boolean isPostMarkedAsFavorite(Long postId, Long userId);
    List<Post> getFavoritePostsById(Long userId);
}
