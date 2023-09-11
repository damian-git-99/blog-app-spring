package com.blog.app.config.security;

import com.blog.app.config.security.authentication.JWTAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationUtils {


    private AuthenticationUtils() {
    }

    /**
     * Obtains the authenticated user from the security context.
     *
     * @return The authenticated user's JWTAuthentication object.
     * @throws RuntimeException if the user is not authenticated.
     */
    public JWTAuthentication getAuthenticatedUser() {
        log.debug("Getting authenticated user from security context");
        JWTAuthentication principal = (JWTAuthentication) SecurityContextHolder
                .getContext().getAuthentication();

        if (principal == null) {
            log.debug("Error getting authenticated user from security context");
            throw new RuntimeException("User not authenticated");
        }

        return principal;
    }

    /**
     * Checks whether a user is authenticated in the current session.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    public boolean isUserAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !isAnonymous(auth);
    }

    private boolean isAnonymous(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS"));
    }

}
