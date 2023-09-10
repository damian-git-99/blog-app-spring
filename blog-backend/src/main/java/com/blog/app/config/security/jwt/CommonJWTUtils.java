package com.blog.app.config.security.jwt;

import com.blog.app.user.dto.UserInfoResponseDTO;

import java.util.HashMap;
import java.util.Map;

public class CommonJWTUtils {

    static public Map<String, Object> createClaims(UserInfoResponseDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("id", user.getId());
        return claims;
    }

}
