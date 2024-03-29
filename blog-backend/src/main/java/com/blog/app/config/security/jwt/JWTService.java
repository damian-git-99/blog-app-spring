package com.blog.app.config.security.jwt;

import java.util.Date;
import java.util.Map;

public interface JWTService {
    /**
     * Creates a JSON Web Token (JWT) with the provided subject and payload.
     *
     * @param subject The subject of the token.
     * @param payload The payload data to be included in the token.
     * @return The JWT as a string.
     */
    String createToken(String subject, Map<String, Object> payload);

    /**
     * Creates a JSON Web Token (JWT) with the provided subject
     *
     * @param subject The subject of the token.
     * @return The JWT as a string.
     */
    String createToken(String subject);

    /**
     * Creates a JSON Web Token (JWT) with the provided payload and expiration date.
     *
     * @param payload    The payload to be included in the token.
     * @param expiration The expiration date of the token.
     * @return The JWT as a string.
     */
    String createToken(Map<String, Object> payload, Date expiration);

    /**
     * Validates a JSON Web Token (JWT) extracted from the authorization header.
     *
     * @param header The Authorization header containing the JWT.
     * @return True if the token is valid; otherwise, it throws a RuntimeException.
     * @throws RuntimeException If the token validation fails (expired or invalid token).
     */
    boolean validateToken(String header);

    /**
     * Extracts and retrieves the claims from a JSON Web Token (JWT) represented by the authorization header.
     *
     * @param authorizationHeader The Authorization header containing the JWT.
     * @return The JWT claims as a Map object.
     */
    Map<String, Object> getClaims(String authorizationHeader);
}
