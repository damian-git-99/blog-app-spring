package com.blog.app.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = "damian";
        String password = "123456";
        Collection<? extends GrantedAuthority> authorities = Stream.of("ROLE_USER")
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new org.springframework.security.core.userdetails.User(email,
                password,
                authorities);
    }

}
