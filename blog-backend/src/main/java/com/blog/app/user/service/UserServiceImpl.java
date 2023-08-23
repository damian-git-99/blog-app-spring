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

    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserDao userDao) {
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    @Transactional
    public void registerUser(User user) {
        log.info("Registering user: {}", user.getEmail());
        validateUserDoesNotExist(user);
        hashUserPassword(user);
        userDao.saveUser(user);
        log.info("User registered successfully: {}", user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findAndValidateUserByEmail(email);
        return createUserDetails(
                user.getEmail(),
                user.getPassword(),
                createEmptyAuthorities()
        );
    }

    private User findAndValidateUserByEmail(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = findUserByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("User not found: {}", email);
            throw new UsernameNotFoundException("User not found");
        }
        return optionalUser.get();
    }

    private Collection<? extends GrantedAuthority> createEmptyAuthorities() {
        return Collections.emptyList();
    }

    private UserDetails createUserDetails(String email, String password,
                                          Collection<? extends GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(email, password, authorities);
    }

    private void validateUserDoesNotExist(User user) {
        Optional<User> optionalUser = findUserByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }
    }

    private void hashUserPassword(User user) {
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
    }

}
