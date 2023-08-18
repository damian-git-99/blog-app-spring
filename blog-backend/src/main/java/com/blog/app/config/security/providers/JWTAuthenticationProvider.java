package com.blog.app.config.security.providers;

import com.blog.app.config.security.authentication.JWTAuthentication;
import com.blog.app.config.security.jwt.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * Custom implementation of Spring Security's AuthenticationProvider for JWT-based authentication.
 * This provider validates and processes JWT tokens, updating the authentication object accordingly.
 */
@Slf4j
public class JWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTService jwtService;


    public JWTAuthenticationProvider(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JWTAuthentication jwtAuthentication = (JWTAuthentication) authentication;
        String token = jwtAuthentication.getToken();
        if (!jwtService.validateToken(token)) {
            log.error("Invalid token");
            return jwtAuthentication;
        }

        jwtAuthentication.setAuthenticated(true);
        Map<String, Object> claims = jwtService.getClaims(jwtAuthentication.getToken());
        String subject = (String) claims.get("sub");
        jwtAuthentication.setName(subject);
        jwtAuthentication.setEmail((String) claims.get("email"));
        jwtAuthentication.setUserId((Long) claims.get("id"));
        return jwtAuthentication;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthentication.class.equals(authentication);
    }

}
