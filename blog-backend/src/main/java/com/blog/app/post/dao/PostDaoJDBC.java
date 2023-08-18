package com.blog.app.post.dao;

import com.blog.app.post.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        int res = jdbc.update(
                query, post.getTitle(),
                post.getContent(),
                post.getSummary(),
                post.getImage(),
                post.getCategory(),
                post.getTime_to_read(),
                post.isPublish(),
                post.getCreated_at(),
                post.getUpdated_at(),
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
        int res = jdbc.update(
                query,
                post.getTitle(),
                post.getContent(),
                post.getSummary(),
                post.getImage(),
                post.getCategory(),
                post.getTime_to_read(),
                post.isPublish(),
                post.getUpdated_at(),
                post.getId()
        );
        return res == 1;
    }

    @Override
    public boolean deletePostById(Long id) {
        String query = "DELETE FROM posts WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        int res = jdbc.update(query, id);
        if (res == 1) log.info("Post deleted successfully");
        else log.error("Error deleting post");
        return res == 1;
    }

    @Override
    public List<Post> getRecentlyPublishedPosts() {
        String query = "SELECT * FROM posts WHERE isPublish = 1 ORDER BY id DESC LIMIT 50";
        log.info("Executing SQL query: {}", query);
        List<Post> posts = jdbc.query(query, BeanPropertyRowMapper.newInstance(Post.class));
        return posts;
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        String query = "SELECT * FROM posts WHERE user_id = ?";
        log.info("Executing SQL query: {}", query);
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
        try {
            Post post = jdbc.queryForObject(
                    query,
                    BeanPropertyRowMapper.newInstance(Post.class),
                    postId
            );
            return Optional.ofNullable(post);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean togglePublicationStatus(Long postId) {
        String query = "UPDATE posts SET isPublish = NOT isPublish WHERE id = ?";
        log.info("Executing SQL query: {}", query);
        int res = jdbc.update(query);
        if (res == 1) log.info("Posts publication status toggled successfully");
        else log.error("Error toggling posts publication status");
        return res == 1;
    }
}
