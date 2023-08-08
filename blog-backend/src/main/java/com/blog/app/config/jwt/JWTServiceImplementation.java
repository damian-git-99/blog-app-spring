package com.blog.app.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class JWTServiceImplementation implements JWTService {

    private final Date TWO_HOURS = new Date(System.currentTimeMillis() + (10 * 6000 * 120));

    public static final Key SECRET_KEY =
            Keys.hmacShaKeyFor("MI_SUPER_LLAVE_PRIVADA_QWERTY_54321".getBytes(StandardCharsets.UTF_8));

    @Override
    public String createToken(String subject, Map<String, Object> payload) {
        Claims claims = Jwts.claims();
        claims.putAll(payload);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(TWO_HOURS)
                .signWith(SECRET_KEY)
                .compact();
    }

    @Override
    public String createToken(String subject) {
        return createToken(subject, null);
    }

    @Override
    public boolean validateToken(String authorizationHeader) {
        try {
            getClaims(authorizationHeader);
            return true;
        } catch (JwtException e) {
            // todo: throw custom exception or just return false?
            log.error("JWT validation failed: {}", e.getMessage());
            throw new RuntimeException("Expired or invalid JWT token");
        }
    }

    @Override
    public Claims getClaims(String authorizationHeader) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(this.resolveToken(authorizationHeader))
                .getBody();
    }

    /**
     * Resolves an authorization token from the provided header.
     *
     * @param header The header string containing the authorization token.
     * @return The extracted authorization token, or the original header string if it cannot be resolved.
     */
    private String resolveToken(String header) {
        // Check if the header is null
        if (header == null) return "";
        // Check if the header starts with the "Bearer " prefix.
        if (header.startsWith("Bearer "))
            // If it starts with "Bearer ", return the string without the prefix.
            return header.replace("Bearer ", "");
        // If it does not start with "Bearer ", return the original string without modification.
        return header;
    }

}