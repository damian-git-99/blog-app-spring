package com.blog.app.user.auth.service;

import com.blog.app.user.dao.UserDao;
import com.blog.app.user.exceptions.UserAlreadyExistsException;
import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;


    public AuthServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void registerUser(User user) {
        log.info("Registering user: {}", user.getEmail());
        Optional<User> optionalUser = userDao.findUserByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userDao.saveUser(user);
        log.info("User registered successfully: {}", user.getEmail());
    }

    @Override
    public void recoverPassword(String email) {

    }

    @Override
    public void resetPasswordCheck(String token) {

    }

    @Override
    public void resetPassword(String token, String newPassword) {

    }

}
