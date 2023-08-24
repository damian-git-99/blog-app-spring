package com.blog.app.user.service;

import com.blog.app.user.dao.UserDao;
import com.blog.app.user.exceptions.UserAlreadyExistsException;
import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = findUserByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("User not found: {}", email);
            throw new UsernameNotFoundException("User not found");
        }
        User user = optionalUser.get();
        return createUserDetails(
                user.getEmail(),
                user.getPassword(),
                createEmptyAuthorities()
        );
    }

    private Collection<? extends GrantedAuthority> createEmptyAuthorities() {
        return Collections.emptyList();
    }

    private UserDetails createUserDetails(String email, String password,
                                          Collection<? extends GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(email, password, authorities);
    }
}
