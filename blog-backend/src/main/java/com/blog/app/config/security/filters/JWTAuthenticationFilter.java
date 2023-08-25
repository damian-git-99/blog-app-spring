package com.blog.app.config.security.filters;

import com.blog.app.config.security.authentication.JWTAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        Optional<String> optionalToken = extractTokenFromRequest(request);

        if (optionalToken.isPresent()) {
            String token = optionalToken.get();
            log.debug("token found: {}", token);
            JWTAuthentication jwtAuthentication = new JWTAuthentication(token);
            Authentication auth = authenticationManager.authenticate(jwtAuthentication);
            if (auth.isAuthenticated()) {
                log.info("Authentication successful by token");
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.debug("Bad Token: Clearing Spring Security Context");
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            }

        }

        chain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        log.debug("Checking token in cookies");
        if (cookies == null) return Optional.empty();
        var optionalCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst();
        return optionalCookie.map(Cookie::getValue);
    }
}
