package com.blog.app.user.service;

import com.blog.app.config.security.AuthenticationUtils;
import com.blog.app.config.security.authentication.AuthenticatedUser;
import com.blog.app.user.dao.UserDao;
import com.blog.app.user.exceptions.UserAlreadyExistsException;
import com.blog.app.user.exceptions.UserNotFoundException;
import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static com.blog.app.common.CommonUtils.mergeNullableFields;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationUtils authenticationUtils;

    @Autowired
    public UserServiceImpl(
            UserDao userDao,
            PasswordEncoder passwordEncoder,
            AuthenticationUtils authenticationUtils) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationUtils = authenticationUtils;
    }

    @Override
    public User findUserById(Long id) {
        return userDao.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
    }

    @Override
    public User getAuthenticatedUserInfo() {
        AuthenticatedUser auth = authenticationUtils.getAuthenticatedUser();
        return findUserById(auth.getUserId());
    }

    @Override
    public boolean editUser(User user) {
        Optional<User> userToEdit = userDao.findUserById(user.getId());
        if (userToEdit.isEmpty()) {
            throw new UserNotFoundException("User not found: " + user.getId());
        }
        User oldUser = userToEdit.get();
        AuthenticatedUser auth = authenticationUtils.getAuthenticatedUser();
        ensureEditPermissionUser(user, auth);
        ensureUsernameAndEmailAreUnique(user.getUsername(), user.getEmail(), oldUser);
        user.setEmail(mergeNullableFields(oldUser.getEmail(), user.getEmail()));
        user.setUsername(mergeNullableFields(oldUser.getUsername(), user.getUsername()));
        user.setPassword(mergeNullableFields(oldUser.getPassword(), passwordEncoder.encode(user.getPassword())));
        return userDao.editUser(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao
                .findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao
                .findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    @Override
    public void addFavoritePost(Long postId) {
        AuthenticatedUser auth = authenticationUtils.getAuthenticatedUser();
        userDao.addFavoritePost(postId, auth.getUserId());
    }

    @Override
    public void removeFavoritePost(Long postId) {
        AuthenticatedUser auth = authenticationUtils.getAuthenticatedUser();
        userDao.removeFavoritePost(postId, auth.getUserId());
    }

    @Override
    public boolean isPostMarkedAsFavorite(Long postId) {
        AuthenticatedUser auth = authenticationUtils.getAuthenticatedUser();
        return userDao.isPostMarkedAsFavorite(postId, auth.getUserId());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userDao.findUserByEmail(email);
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

    /**
     * Ensures that a username and an email address are unique, considering the possibility of updating an existing user.
     *
     * @param username The username to be checked for uniqueness.
     * @param email    The email address to be checked for uniqueness.
     * @param oldUser  The existing user object for comparison (to allow updates without enforcing uniqueness).
     * @throws UserAlreadyExistsException If the email address and username combination already exists in the database.
     */
    private void ensureUsernameAndEmailAreUnique(String username, String email, User oldUser) {

        if (oldUser.getEmail().equals(email) && oldUser.getUsername().equals(username)) {
            // If the email and username match the existing user, no uniqueness violation occurs.
            return;
        }

        Optional<User> optionalUser = userDao.findUserByEmailOrUsername(email, username);
        if (optionalUser.isPresent()) {
            log.warn("Username or email already exists");

            if (optionalUser.get().getEmail().equals(email)
                    && optionalUser.get().getUsername().equals(username)) {
                throw new UserAlreadyExistsException("Email and username already exists");
            }

            if (optionalUser.get().getEmail().equals(email)) {
                throw new UserAlreadyExistsException("Email already exists");
            }

            if (optionalUser.get().getUsername().equals(username)) {
                throw new UserNotFoundException("Username already exists");
            }
        }
    }

    /**
     * Ensures that the authenticated user has permission to edit the specified user.
     *
     * @param user The user to be edited.
     * @param auth The AuthenticatedUser object representing the authenticated user.
     * @throws RuntimeException If the authenticated user does not have permission to edit the specified user.
     */
    private void ensureEditPermissionUser(User user, AuthenticatedUser auth) {
        if (!Objects.equals(auth.getUserId(), user.getId())) {
            // todo: throw custom exception
            throw new RuntimeException("You are not allowed to edit this user");
        }
    }

}
