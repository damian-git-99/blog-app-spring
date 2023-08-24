package com.blog.app.user.auth.service;

import com.blog.app.common.email.EmailService;
import com.blog.app.config.security.jwt.JWTService;
import com.blog.app.user.dao.UserDao;
import com.blog.app.user.exceptions.UserAlreadyExistsException;
import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final EmailService emailService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;


    public AuthServiceImpl(
            UserDao userDao,
            PasswordEncoder passwordEncoder,
            JWTService jwtService,
            EmailService emailService
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
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
        Optional<User> optionalUser = userDao.findUserByEmail(email);
        if (optionalUser.isEmpty()) {
            // todo: add custom exception
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        String token = createToken(user);
        String body = emailBody(token);
        System.out.println(token);
        emailService.sendEmail(email, "Reset Password", body);
        log.info("Email sent to: {}", email);
    }

    @Override
    public void resetPasswordCheck(String token) {
        if (!jwtService.validateToken(token)) {
            // todo: add custom exception
            throw new RuntimeException("Invalid token");
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        if (!jwtService.validateToken(token)) {
            // todo: add custom exception
            throw new RuntimeException("Invalid token");
        }

        Map<String, Object> claims = jwtService.getClaims(token);
        String email = (String) claims.get("email");
        Optional<User> optionalUser = userDao.findUserByEmail(email);
        if (optionalUser.isEmpty()) {
            // todo: add custom exception
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        String hashedPassword = this.passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userDao.editUser(user);
        log.info("Password reset successfully: {}", email);
    }

    private String createToken(User user) {
        Date FIVE_MINUTES = new Date(System.currentTimeMillis() + (5 * 60 * 1000));
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("id", user.getId());
        return jwtService.createToken(claims, FIVE_MINUTES);
    }

    private String emailBody(String token) {
        String link = frontendBaseUrl + "/reset-password?token=" + token;
        return "<div>\n" +
                "  <b>Please click below link to reset your password</b>\n" +
                "</div>\n" +
                "<div>\n" +
                "  <a href=\"" + link + "\">Reset</a>\n" +
                "</div>";
    }


}
