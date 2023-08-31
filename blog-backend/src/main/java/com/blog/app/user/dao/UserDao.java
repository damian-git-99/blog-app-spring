package com.blog.app.user.dao;

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
    // todo: addFavoritePost
    // todo: removeFavoritePost
    // todo: isPostFavorite
    // todo: getFavoritePosts
}
