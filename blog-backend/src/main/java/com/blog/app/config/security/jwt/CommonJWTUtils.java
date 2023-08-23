package com.blog.app.config.security.jwt;

import com.blog.app.user.model.User;

import java.util.HashMap;
import java.util.Map;

public class CommonJWTUtils {

    static public Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("id", user.getId());
        return claims;
    }

}
