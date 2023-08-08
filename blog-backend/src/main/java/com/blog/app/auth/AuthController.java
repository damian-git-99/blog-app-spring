package com.blog.app.auth;

import com.blog.app.user.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    // todo: implement token and register

    @PostMapping("/register")
    String register(@RequestBody User user) {
        System.out.println(user.getUsername());
        return "register";
    }

    @PostMapping("/logout")
    String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "cookie removed";
    }

    @GetMapping("/verify-token")
    String verifyToken() {
        return "";
    }

}
