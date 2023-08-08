package com.blog.app.config.filters;

import com.blog.app.config.jwt.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final ObjectMapper mapper = new ObjectMapper();

    public JWTAuthenticationFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        log.info("Checking token in cookies");

        if (cookies != null) {
            var optionalCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("token"))
                    .findFirst();

            if (optionalCookie.isPresent()) {
                String token = optionalCookie.get().getValue();
                log.info("token found: {}", token);
                Cookie cookie = optionalCookie.get();
                Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
                try {
                    if (jwtService.validateToken(cookie.getValue())) {
                        Map claims = jwtService.getClaims(cookie.getValue());
                        String subject = (String) claims.get("subject");
                        System.out.println(claims);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.info("Authentication successful by token");
                    }
                } catch (Exception ex) {
                    log.info("Clearing Spring Security Context");
                    SecurityContextHolder.clearContext();
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("error", ex.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(response.getWriter(), errors);
                }
            }

        }

        chain.doFilter(request, response);
    }
}
