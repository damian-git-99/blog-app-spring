package com.blog.app.config.providers;

import com.blog.app.config.authentication.JWTAuthentication;
import com.blog.app.config.jwt.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

@Slf4j
public class JWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTService jwtService;


    public JWTAuthenticationProvider(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JWTAuthentication jwtAuthentication = (JWTAuthentication) authentication;
        try {
            jwtService.validateToken(jwtAuthentication.getToken());
            jwtAuthentication.setAuthenticated(true);
            Map claims = jwtService.getClaims(jwtAuthentication.getToken());
            String subject = (String) claims.get("sub");
            jwtAuthentication.setName(subject);
            return jwtAuthentication;
        } catch (Exception e) {
            log.error("Invalid token");
            return jwtAuthentication;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthentication.class.equals(authentication);
    }

}
