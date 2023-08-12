package com.blog.app.user.dao;

import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class UserDaoJDBC implements UserDao {

    private final JdbcTemplate jdbc;

    public UserDaoJDBC(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        try {
            String query = "SELECT * FROM users WHERE email = ?";
            log.info("Executing SQL query: {}", query);
            User user = jdbc.queryForObject(
                    query,
                    BeanPropertyRowMapper.newInstance(User.class),
                    email
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean saveUser(User user) {
        String query = "INSERT INTO users (email, password, username) VALUES (?, ?, ?)";
        log.info("Executing SQL query: {}", query);
        int result = jdbc.update(
                query,
                user.getEmail(),
                user.getPassword(),
                user.getUsername()
        );
        return result == 1;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        try {
            User user = jdbc.queryForObject(
                    query,
                    BeanPropertyRowMapper.newInstance(User.class),
                    id
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean editUser(User user) {
        String query = "UPDATE users SET email = ?, password = ?, username = ? WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        int res = jdbc.update(
                query,
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getId()
        );
        return res == 1;
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        log.info("Executing SQL query: {}", query);
        try {
            User user = jdbc.queryForObject(
                    query,
                    BeanPropertyRowMapper.newInstance(User.class),
                    username
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
