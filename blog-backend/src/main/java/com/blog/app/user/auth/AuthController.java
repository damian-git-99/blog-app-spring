package com.blog.app.user.auth;

import com.blog.app.config.security.jwt.JWTService;
import com.blog.app.user.auth.service.AuthService;
import com.blog.app.user.model.User;
import com.blog.app.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.blog.app.config.security.jwt.CommonJWTUtils.createClaims;

@RestController
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JWTService jwtService;

    @Autowired
    public AuthController(AuthService authService, UserService userService, JWTService jwtService) {
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
        return ResponseEntity.ok("register Successfully");
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
                createResponseBody(optionalUser.get())
        );
    }

    private Map<String, Object> createResponseBody(User user) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", user.getId());
        body.put("username", user.getUsername());
        body.put("email", user.getEmail());
        return body;
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
