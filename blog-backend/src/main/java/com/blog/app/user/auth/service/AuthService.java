package com.blog.app.user.auth.service;

import com.blog.app.user.model.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthService {

    User registerUser(User user);

    User googleLogin(String clientId) throws GeneralSecurityException, IOException;

    void recoverPassword(String email);

    void resetPasswordCheck(String token);

    void resetPassword(String token, String newPassword);

}
