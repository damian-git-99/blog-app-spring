package com.blog.app.user.auth.service;

import com.blog.app.user.model.User;

public interface AuthService {

    void registerUser(User user);

    void recoverPassword(String email);

    void resetPasswordCheck(String token);

    void resetPassword(String token, String newPassword);

}
