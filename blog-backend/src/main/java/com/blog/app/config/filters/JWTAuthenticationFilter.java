package com.blog.app.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {

            var optionalCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("token"))
                    .findFirst();

            if (optionalCookie.isPresent()) {
                Cookie cookie = optionalCookie.get();
                Collection<? extends GrantedAuthority> authorities = Stream.of("ROLE_USER")
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                // todo: check if token is valid
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(cookie.getValue(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(token);
            }

        }

        chain.doFilter(request, response);

    }
}
