package com.blog.app.user.auth;

import com.blog.app.config.security.jwt.JWTService;
import com.blog.app.user.dto.UserMapper;
import com.blog.app.user.auth.service.AuthService;
import com.blog.app.user.model.User;
import com.blog.app.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

import static com.blog.app.common.CommonUtils.handleValidationExceptions;
import static com.blog.app.config.security.jwt.CommonJWTUtils.createClaims;

@RestController
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JWTService jwtService;

    @Autowired
    public AuthController(
            AuthService authService,
            UserService userService,
            JWTService jwtService
    ) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody @Valid User user, BindingResult br) {
        if (br.hasErrors()) {
            return handleValidationExceptions(br);
        }
        authService.registerUser(user);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping("/logout")
    String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "cookie removed";
    }

    @GetMapping("/verify-token")
    ResponseEntity<?> verifyToken(HttpServletResponse response, Principal principal) {
        Optional<User> optionalUser = userService.findUserByEmail(principal.getName());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("user not found");
        }
        User user = optionalUser.get();
        String token = jwtService.createToken(user.getEmail(), createClaims(user));
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(cookie);
        return ResponseEntity.ok(
                UserMapper.INSTANCE.toUserInfoResponseDTO(user)
        );
    }

    @PostMapping("/recover-password")
    @ResponseStatus(HttpStatus.OK)
    void recoverPassword(@RequestBody UserRequest userRequest) {
        String email = userRequest.getEmail();
        authService.recoverPassword(email);
    }


    @GetMapping("/reset-password/{token}")
    @ResponseStatus(HttpStatus.OK)
    void resetPasswordCheck(@PathVariable String token) {
        authService.resetPasswordCheck(token);
    }

    @PostMapping("/reset-password/{token}")
    @ResponseStatus(HttpStatus.OK)
    void resetPassword(@PathVariable String token, @RequestBody UserRequest userRequest) {
        String password = userRequest.getPassword();
        authService.resetPassword(token, password);
    }

    @Data
    static class UserRequest {
        private String email;
        private String password;
    }

}
