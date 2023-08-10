package com.blog.app.user.dao;

import com.blog.app.user.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDaoJDBC implements UserDao {

    private final JdbcTemplate jdbc;

    public UserDaoJDBC(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        try {
            String query = "SELECT * FROM users WHERE email = ?";
            User user = jdbc.queryForObject(query, BeanPropertyRowMapper.newInstance(User.class), email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean saveUser(User user) {
        String query = "INSERT INTO users (email, password, username) VALUES (?, ?, ?)";
        int result = jdbc.update(query, user.getEmail(), user.getPassword(), user.getUsername());
        return result == 1;
    }
}
