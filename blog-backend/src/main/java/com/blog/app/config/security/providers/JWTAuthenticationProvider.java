package com.blog.app.config.security.providers;

import com.blog.app.config.security.authentication.AuthenticatedUser;
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
        log.info("Attempting to authenticate by JWT token");
        log.debug("AuthenticatedUser: {}", authentication);
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication;
        String token = authenticatedUser.getToken();
        if (!jwtService.validateToken(token)) {
            return authenticatedUser;
        }
        authenticatedUser.setAuthenticated(true);
        Map<String, Object> claims = jwtService.getClaims(token);
        String subject = (String) claims.get("sub");
        authenticatedUser.setName(subject);
        authenticatedUser.setEmail((String) claims.get("sub"));
        authenticatedUser.setUsername((String) claims.get("username"));
        Long userId = Long.valueOf((Integer) claims.get("id"));
        authenticatedUser.setUserId(userId);
        log.debug("token data was put in the AuthenticatedUser");
        return authenticatedUser;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthenticatedUser.class.equals(authentication);
    }

}
