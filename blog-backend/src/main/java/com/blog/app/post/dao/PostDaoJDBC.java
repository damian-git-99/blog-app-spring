package com.blog.app.post.dao;

import com.blog.app.post.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class PostDaoJDBC implements PostDao {

    private final JdbcTemplate jdbc;

    public PostDaoJDBC(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean savePost(Post post) {
        String query = "INSERT INTO posts (" +
                "title, " +
                "content, " +
                "summary, " +
                "image, " +
                "category, " +
                "time_to_read, " +
                "isPublish, " +
                "created_at, " +
                "updated_at, " +
                "user_id ) VALUES (?, ?, ?, ?, ? ,? ,?,?,?,?)";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                post.getTitle(),
                post.getContent(),
                post.getSummary(),
                post.getImage(),
                post.getCategory(),
                post.getTime_to_read(),
                post.isPublish(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getUserId()
        );
        int res = jdbc.update(
                query, post.getTitle(),
                post.getContent(),
                post.getSummary(),
                post.getImage(),
                post.getCategory(),
                post.getTime_to_read(),
                post.isPublish(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getUserId()
        );
        if (res == 1) log.info("Post saved successfully");
        else log.error("Error saving post");
        return res == 1;
    }

    @Override
    public boolean editPost(Post post) {
        String query = "UPDATE posts SET " +
                "title = ?, " +
                "content = ?, " +
                "summary = ?, " +
                "image = ?, " +
                "category = ?, " +
                "time_to_read = ?, " +
                "isPublish = ?, " +
                "updated_at = ?, " +
                "WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}, {}, {}, {}, {}, {}, {}, {}, {}",
                post.getTitle(),
                post.getContent(),
                post.getSummary(),
                post.getImage(),
                post.getCategory(),
                post.getTime_to_read(),
                post.isPublish(),
                post.getUpdatedAt(),
                post.getId()
        );
        int res = jdbc.update(
                query,
                post.getTitle(),
                post.getContent(),
                post.getSummary(),
                post.getImage(),
                post.getCategory(),
                post.getTime_to_read(),
                post.isPublish(),
                post.getUpdatedAt(),
                post.getId()
        );
        return res == 1;
    }

    @Override
    public boolean deletePostById(Long id) {
        String query = "DELETE FROM posts WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", id);
        int res = jdbc.update(query, id);
        if (res == 1) log.info("Post deleted successfully");
        else log.error("Error deleting post");
        return res == 1;
    }

    @Override
    public List<Post> getRecentlyPublishedPosts() {
        String query = "SELECT * FROM posts WHERE isPublish = 1 ORDER BY id DESC LIMIT 50";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(query, BeanPropertyRowMapper.newInstance(Post.class));
        return posts;
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        String query = "SELECT * FROM posts WHERE user_id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(
                query,
                BeanPropertyRowMapper.newInstance(Post.class),
                userId
        );
        return posts;
    }

    @Override
    public List<Post> getPublicPostsByUsername(String username) {
        String query = "SELECT * FROM posts WHERE user_id = (SELECT id FROM users WHERE username = ?) AND posts.isPublish = 1";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(
                query,
                BeanPropertyRowMapper.newInstance(Post.class),
                username
        );
        return posts;
    }

    @Override
    public List<Post> getAllPostsByUsername(String username) {
        String query = "SELECT * FROM posts WHERE user_id = (SELECT id FROM users WHERE username = ?)";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(
                query,
                BeanPropertyRowMapper.newInstance(Post.class),
                username
        );
        return posts;
    }

    @Override
    public Optional<Post> getPostById(Long postId) {
        String query = "SELECT * FROM posts WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        try {
            Post post = jdbc.queryForObject(
                    query,
                    (rs, rowNum) -> mapPost(rs),
                    postId
            );
            return Optional.ofNullable(post);
        } catch (Exception e) {
            System.out.println(e);
            return Optional.empty();
        }
    }

    private Post mapPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setSummary(rs.getString("summary"));
        post.setContent(rs.getString("content"));
        post.setImage(rs.getString("image"));
        post.setCategory(rs.getString("category"));
        post.setTime_to_read(rs.getInt("time_to_read"));
        post.setPublish(rs.getBoolean("isPublish"));
        post.setUserId(rs.getLong("user_id"));
        post.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        post.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return post;
    }

    @Override
    public boolean togglePublicationStatus(Long postId) {
        String query = "UPDATE posts SET isPublish = NOT isPublish WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        int res = jdbc.update(query);
        if (res == 1) log.info("Posts publication status toggled successfully");
        else log.error("Error toggling posts publication status");
        return res == 1;
    }
}
