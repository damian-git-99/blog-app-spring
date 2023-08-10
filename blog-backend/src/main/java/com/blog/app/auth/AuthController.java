package com.blog.app.auth;

import com.blog.app.user.model.User;
import com.blog.app.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody @Valid User user, BindingResult br) {
        if (br.hasErrors()) {
            return handleValidationExceptions(br);
        }
        userService.registerUser(user);
        return ResponseEntity.ok("register Successfully 2");
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
        // todo : implement
        return "";
    }

    /**
     * It handles validation exceptions generated by validation errors in the data binding process
     * between input data and Java objects.
     *
     * @param br The BindingResult object that contains the validation results.
     * @return ResponseEntity with a body containing a map of validation errors if any.
     */
    public ResponseEntity<?> handleValidationExceptions(BindingResult br) {
        Map<String, Object> errorsMap = new HashMap<>();
        br.getFieldErrors()
                .forEach(objectError -> errorsMap.put(objectError.getField(), objectError.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errorsMap);
    }

}
