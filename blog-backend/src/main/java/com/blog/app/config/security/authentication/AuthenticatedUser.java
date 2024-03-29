package com.blog.app.config.security.authentication;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents an authenticated user in the application.
 */
@Data
@NoArgsConstructor
public class AuthenticatedUser implements Authentication {

    private boolean isAuthenticated;
    private String email;
    private String username;
    private Long userId;

    public AuthenticatedUser(String username, Long userId) {
        this.username = username;
        this.userId = userId;
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

}
