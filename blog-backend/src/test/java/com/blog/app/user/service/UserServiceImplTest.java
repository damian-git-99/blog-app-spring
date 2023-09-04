package com.blog.app.user.service;

import com.blog.app.config.security.AuthenticationUtils;
import com.blog.app.config.security.authentication.JWTAuthentication;
import com.blog.app.user.dao.UserDao;
import com.blog.app.user.exceptions.UserNotFoundException;
import com.blog.app.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationUtils authenticationUtils;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
    }

    @Nested
    class FindUserByIdTests {

        @Test
        public void testFindUserById_UserFound() {
            Long userId = 1L;
            User user = new User(userId, "username", "email", "password");
            when(userDao.findUserById(userId)).thenReturn(Optional.of(user));

            Optional<User> result = userService.findUserById(userId);
            assertThat(result)
                    .isPresent()
                    .hasValue(user);

            verify(userDao).findUserById(userId);
        }

        @Test
        public void testFindUserById_UserNotFound() {
            Long userId = 2L;
            when(userDao.findUserById(userId)).thenReturn(Optional.empty());

            Optional<User> result = userService.findUserById(userId);
            assertThat(result).isEmpty();

            verify(userDao).findUserById(userId);
        }

    }

    @Nested
    class EditUserTests {

        @Test
        public void testEditUser_UserFound_EditSuccessful() {
            Long userId = 1L;
            User existingUser = new User(userId, "username", "email", "password");
            when(userDao.findUserById(userId)).thenReturn(Optional.of(existingUser));

            JWTAuthentication auth = new JWTAuthentication("username", 1L);

            when(authenticationUtils.getAuthenticatedUser()).thenReturn(auth);

            User updatedUser = new User(userId, "newUsername", "newEmail", "newPassword");
            when(userDao.editUser(updatedUser)).thenReturn(true);

            boolean editResult = userService.editUser(updatedUser);
            assertThat(editResult).isTrue();

            verify(userDao).findUserById(userId);
            verify(userDao).editUser(any(User.class));
        }

        @Test
        public void testEditUser_UserNotFound() {
            Long userId = 2L;
            when(userDao.findUserById(userId)).thenReturn(Optional.empty());

            User userToEdit = new User(userId, "newUsername", "newEmail", "newPassword");
            assertThatExceptionOfType(UserNotFoundException.class)
                    .isThrownBy(() -> userService.editUser(userToEdit))
                    .withMessage("User not found: " + userId);

            verify(userDao).findUserById(userId);
            verify(userDao, never()).editUser(any(User.class));
        }

        @Test
        public void testEnsureEditPermissionUser_UserPermissionDenied() {

            Long userId = 1L;
            User existingUser = new User(userId, "username", "email", "password");
            when(userDao.findUserById(userId)).thenReturn(Optional.of(existingUser));

            JWTAuthentication auth = new JWTAuthentication("username", 2L);
            when(authenticationUtils.getAuthenticatedUser()).thenReturn(auth);

            User userToEdit = new User(1L, "newUsername", "newEmail", "newPassword");

            assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(() -> userService.editUser(userToEdit))
                    .withMessage("You are not allowed to edit this user");

        }


        @Test
        public void testEditUser_NonUniqueUsername() {
            Long userId = 2L;
            User existingUser = new User(userId, "newUsername", "newEmail", "password");
            when(userDao.findUserById(userId)).thenReturn(Optional.of(existingUser));

            JWTAuthentication auth = new JWTAuthentication("username", 1L);
            when(authenticationUtils.getAuthenticatedUser()).thenReturn(auth);

            User userToEdit = new User(userId, "newUsername", "newEmail", "newPassword");
            assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(() -> userService.editUser(userToEdit))
                    .withMessage("You are not allowed to edit this user");

            verify(userDao).findUserById(userId);
        }

        @Test
        public void testEditUser_NonUniqueEmail() {
            Long userId = 2L;
            User existingUser = new User(userId, "username", "newEmail", "password");
            when(userDao.findUserById(userId)).thenReturn(Optional.of(existingUser));

            JWTAuthentication auth = new JWTAuthentication("username", 1L);
            when(authenticationUtils.getAuthenticatedUser()).thenReturn(auth);

            User userToEdit = new User(userId, "username", "newEmail", "newPassword");
            assertThatExceptionOfType(RuntimeException.class)
                    .isThrownBy(() -> userService.editUser(userToEdit))
                    .withMessage("You are not allowed to edit this user");

            verify(userDao).findUserById(userId);
        }

    }

    @Nested
    class FindUserByUsernameTests {
        @Test
        public void testFindUserByUsername_UserFound() {
            String username = "testuser";
            User user = new User(1L, username, "email", "password");
            when(userDao.findUserByUsername(username)).thenReturn(Optional.of(user));

            Optional<User> result = userService.findUserByUsername(username);
            assertThat(result)
                    .isPresent()
                    .hasValue(user);

            verify(userDao).findUserByUsername(username);
        }

        @Test
        public void testFindUserByUsername_UserNotFound() {
            String username = "nonexistentuser";
            when(userDao.findUserByUsername(username)).thenReturn(Optional.empty());

            Optional<User> result = userService.findUserByUsername(username);
            assertThat(result).isEmpty();

            verify(userDao).findUserByUsername(username);
        }
    }

    @Nested
    class FindUserByEmailTests {

        @Test
        public void testFindUserByEmail_UserFound() {
            String userEmail = "user@example.com";

            User existingUser = new User(1L, "username", userEmail, "password");
            when(userDao.findUserByEmail(userEmail)).thenReturn(Optional.of(existingUser));

            Optional<User> foundUser = userService.findUserByEmail(userEmail);

            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getEmail()).isEqualTo(userEmail);
        }

        @Test
        public void testFindUserByEmail_UserNotFound() {
            String userEmail = "nonexistent@example.com";

            when(userDao.findUserByEmail(userEmail)).thenReturn(Optional.empty());

            Optional<User> foundUser = userService.findUserByEmail(userEmail);

            assertThat(foundUser).isEmpty();
        }

    }

    @Nested
    class LoadUserByUsernameTests {

        @Test
        public void testLoadUserByUsername_UserFound() {
            String email = "testuser@example.com";
            User user = new User(1L, "username", email, "password");
            when(userDao.findUserByEmail(email)).thenReturn(Optional.of(user));

            UserDetails userDetails = userService.loadUserByUsername(email);
            assertThat(userDetails)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("username", email)
                    .hasFieldOrPropertyWithValue("password", user.getPassword());

            verify(userDao).findUserByEmail(email);
        }

        @Test
        public void testLoadUserByUsername_UserNotFound() {
            String email = "nonexistent@example.com";
            when(userDao.findUserByEmail(email)).thenReturn(Optional.empty());

            assertThatExceptionOfType(UsernameNotFoundException.class)
                    .isThrownBy(() -> userService.loadUserByUsername(email))
                    .withMessage("User not found");

            verify(userDao).findUserByEmail(email);
        }
    }

}