package com.blog.app.config.security.filters;

import com.blog.app.config.security.jwt.JWTService;
import com.blog.app.user.dto.UserInfoResponseDTO;
import com.blog.app.user.dto.UserMapper;
import com.blog.app.user.model.User;
import com.blog.app.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.blog.app.config.security.jwt.CommonJWTUtils.createClaims;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;

    public AuthenticationFilter(
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/login",
                        HttpMethod.POST.toString())
        );
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        log.info("attempt Authentication request");
        String email = "";
        String password = "";

        try {
            Map body = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            email = body.get("email").toString();
            password = body.get("password").toString();
            log.debug("email attempting authentication: {}", email);
        } catch (Exception e) {
            log.error("Error reading request body: {}", e.getMessage());
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain chain
            , Authentication authResult) throws IOException {

        User user = userService.findUserByEmail(authResult.getName());
        String token = jwtService.createToken(authResult.getName(), createClaims(user));
        Cookie cookie = new Cookie("token", token);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        UserInfoResponseDTO dto = UserMapper.INSTANCE.toUserInfoResponseDTO(user);

        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));
        response.setStatus(HttpServletResponse.SC_OK);
        log.info("successful Authentication request: {}", authResult.getName());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request
            , HttpServletResponse response
            , AuthenticationException failed) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Authentication ERROR: incorrect username or password");

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        log.warn("unsuccessful Authentication request: {}", failed.getLocalizedMessage());
    }
}
