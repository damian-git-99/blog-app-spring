package com.blog.app.user.dao;

import com.blog.app.post.model.Post;
import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

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
            log.debug("Param email: {}", email);
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
    public Optional<User> findUserByEmailOrUsername(String email, String username) {
        try {
            String query = "SELECT id, username, email FROM users WHERE email = ? OR username = ? LIMIT 1";
            log.info("Executing SQL query: {}", query);
            log.debug("Params : {} {}", email, username);
            User user = jdbc.queryForObject(
                    query,
                    BeanPropertyRowMapper.newInstance(User.class),
                    email, username
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public User saveUser(User user) {
        String query = "INSERT INTO users (email, password, username) VALUES (?, ?, ?)";
        log.info("Executing SQL query: {}", query);
        log.debug("Param: {}", user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update((connection) -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUsername());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Param id: {}", id);
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
        log.debug("Params: {} {} {} {}", user.getEmail(), user.getPassword(), user.getUsername(), user.getId());
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
        String query = "SELECT id, username, email FROM users WHERE username = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Param username: {}", username);
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

    @Override
    public void addFavoritePost(Long postId, Long userId) {
        String query = "INSERT INTO favorite_posts (post_id, user_id) VALUES (?, ?)";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {} {}", postId, userId);
        jdbc.update(query, postId, userId);
    }

    @Override
    public void removeFavoritePost(Long postId, Long userId) {
        String query = "DELETE FROM favorite_posts WHERE post_id = ? AND user_id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {} {}", postId, userId);
        jdbc.update(query, postId, userId);
    }

    @Override
    public boolean isPostMarkedAsFavorite(Long postId, Long userId) {
        String query = "SELECT * FROM favorite_posts WHERE post_id = ? AND user_id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {} {}", postId, userId);
        try {
            jdbc.queryForObject(query, BeanPropertyRowMapper.newInstance(User.class), postId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}


