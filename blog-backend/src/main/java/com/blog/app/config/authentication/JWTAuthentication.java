package com.blog.app.config.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of the Spring Security Authentication interface for JWT-based authentication.
 * This class represents the authentication state and provides access to token-related information.
 */
public class JWTAuthentication implements Authentication {

    private final String token;
    private boolean isAuthenticated;
    private String email;

    public JWTAuthentication(String token) {
        this.token = token;
        this.isAuthenticated = false;
        this.email = "";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        // return email from token
        return email;
    }

    public void setName(String name) {
        this.email = name;
    }

    public String getToken() {
        return token;
    }
}
