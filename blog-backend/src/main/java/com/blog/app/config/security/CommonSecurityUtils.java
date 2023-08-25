package com.blog.app.config.security;

import com.blog.app.config.security.authentication.JWTAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class CommonSecurityUtils {


    private CommonSecurityUtils() {
    }

    /**
     * Obtains the authenticated user from the security context.
     *
     * @return The authenticated user's JWTAuthentication object.
     * @throws RuntimeException if the user is not authenticated.
     */
    static public JWTAuthentication getAuthenticatedUser() {
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
     * Verifies if the authenticated user matches the provided username.
     *
     * @param username The username for which authentication is being verified.
     * @return true if the authenticated user matches the provided username, false otherwise.
     */
    static public boolean isAuthenticatedUser(String username) {
        JWTAuthentication authenticatedUser = getAuthenticatedUser();
        return authenticatedUser.getUsername().equals(username);
    }

}
