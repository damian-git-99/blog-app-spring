package com.blog.app.post.dao;

import com.blog.app.post.model.Category;
import com.blog.app.post.model.Comment;
import com.blog.app.post.model.Post;
import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        post.setCategory("XDDDDDDDDDD");
        String query = """
                INSERT INTO posts (
                    title,
                    content,
                    summary,
                    image,
                    category,
                    time_to_read,
                    isPublish,
                    created_at,
                    updated_at,
                    user_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                post.getTitle(), post.getContent(), post.getSummary(),
                post.getImage(), post.getCategory(), post.getTime_to_read(),
                post.isPublish(), post.getCreatedAt(), post.getUpdatedAt(),
                post.getUserId()
        );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int res = jdbc.update((connection) -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getSummary());
            ps.setString(4, post.getImage());
            ps.setString(5, post.getCategory());
            ps.setInt(6, post.getTime_to_read());
            ps.setBoolean(7, post.isPublish());
            ps.setObject(8, post.getCreatedAt());
            ps.setObject(9, post.getUpdatedAt());
            ps.setLong(10, post.getUserId());
            return ps;
        }, keyHolder);

        for (Category c : post.getCategories()) {
            c.setPostId(keyHolder.getKey().longValue());
            saveCategory(c);
        }

        if (res == 1) log.info("Post saved successfully");
        else log.error("Error saving post");
        return res == 1;
    }

    private void saveCategory(Category category) {
        String query = """
                INSERT INTO categories (
                    category,
                    post_id
                ) VALUES (?, ?)
                """;
        jdbc.update(query, category.getCategory(), category.getPostId());
    }

    @Override
    public boolean editPost(Post post) {
        String query = """
                UPDATE posts
                SET
                    title = ?,
                    content = ?,
                    summary = ?,
                    image = ?,
                    category = ?,
                    time_to_read = ?,
                    isPublish = ?,
                    updated_at = ?
                WHERE
                    id = ?
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}, {}, {}, {}, {}, {}, {}, {}, {}",
                post.getTitle(), post.getContent(), post.getSummary(),
                post.getImage(), post.getCategory(), post.getTime_to_read(),
                post.isPublish(), post.getUpdatedAt(), post.getId()
        );
        int res = jdbc.update(query,
                post.getTitle(), post.getContent(), post.getSummary(),
                post.getImage(), post.getCategory(), post.getTime_to_read(),
                post.isPublish(), post.getUpdatedAt(), post.getId()
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
        String query = """
                SELECT *
                FROM posts
                JOIN users ON posts.user_id = users.id
                WHERE isPublish = 1
                ORDER BY posts.id DESC
                LIMIT 50
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(query, (rs, rowNum) -> mapPost(rs));
        return posts;
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        String query = "SELECT * FROM posts WHERE user_id = ?";
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(query, BeanPropertyRowMapper.newInstance(Post.class), userId);
        return posts;
    }

    @Override
    public List<Post> getPublicPostsByUsername(String username) {
        String query = """
                SELECT *
                FROM posts
                JOIN users ON posts.user_id = users.id
                WHERE users.username = ? AND posts.isPublish = 1
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(query, (rs, rowNum) -> mapPost(rs), username);
        return posts;
    }

    @Override
    public List<Post> getAllPostsByUsername(String username) {
        String query = """
                SELECT *
                FROM posts
                JOIN users ON posts.user_id = users.id
                WHERE users.username = ?
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        List<Post> posts = jdbc.query(query, (rs, rowNum) -> mapPost(rs), username);
        return posts;
    }

    public Optional<Post> getPostById(Long postId) {
        String query = """
                SELECT
                p.id, p.title, p.summary, p.content, p.image,
                p.category, p.time_to_read, p.isPublish,
                p.user_id, p.created_at,  p.updated_at,
                users.username, users.email,
                c.message, c.created_at as comment_created_at,
                u.username as comment_username, u.email as comment_email
                FROM posts p
                INNER JOIN users
                ON p.user_id = users.id
                LEFT OUTER JOIN comments c
                ON c.post_id = p.id
                LEFT OUTER JOIN users u
                ON c.user_id = u.id
                WHERE p.id = ?
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        try {
            Post post = jdbc.queryForObject(query, (rs, rowNum) -> {
                Post post1 = null;
                do {
                    if (post1 == null) {
                        post1 = mapPostById(rs);
                    }
                    addComment(post1, rs);
                } while (rs.next());
                return post1;
            }, postId);
            return Optional.ofNullable(post);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    private Post mapPostById(ResultSet rs) throws SQLException {
        Post post = mapPost(rs);
        addComment(post, rs);
        return post;
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
        post.setIsPublish(rs.getBoolean("isPublish"));
        post.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        post.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        User user = new User(
                rs.getLong("user_id"),
                rs.getString("username"),
                rs.getString("email"), ""
        );
        post.setUser(user);
        return post;
    }

    private void addComment(Post post, ResultSet rs) throws SQLException {
        String message = rs.getString("message");
        if (message == null) return;
        LocalDateTime commentCreatedAt = rs.getObject("comment_created_at", LocalDateTime.class);
        String commentUsername = rs.getString("comment_username");
        String commentEmail = rs.getString("comment_email");
        User userComment = new User();
        userComment.setEmail(commentEmail);
        userComment.setUsername(commentUsername);
        Comment comment = new Comment(message, userComment, commentCreatedAt);
        post.addComment(comment);
    }

    @Override
    public boolean togglePublicationStatus(Long postId) {
        String query = """
                UPDATE posts
                SET isPublish = CASE
                    WHEN isPublish = 1 THEN 0
                    ELSE 1
                    END
                WHERE id = ?
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Params: {}", query);
        int res = jdbc.update(query, postId);
        if (res == 1) log.info("Posts publication status toggled successfully");
        else log.error("Error toggling posts publication status");
        return res == 1;
    }

    @Override
    public List<Post> getFavoritePostsByUserId(Long userId) {
        String query = """
                SELECT *
                FROM favorite_posts fp
                JOIN users u ON fp.user_id = u.id
                JOIN posts p ON fp.post_id = p.id
                WHERE fp.user_id = ?
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Param userId: {}", userId);
        try {
            return jdbc.query(query, rs -> {
                boolean isFirstRow = true;
                User user = new User();
                List<Post> posts = new ArrayList<>();
                while (rs.next()) {
                    if (isFirstRow) {
                        user.setId(rs.getLong("user_id"));
                        user.setEmail(rs.getString("email"));
                        user.setUsername(rs.getString("username"));
                        isFirstRow = false;
                    }
                    Post p = new Post();
                    p.setId(rs.getLong("post_id"));
                    p.setTitle(rs.getString("title"));
                    p.setContent(rs.getString("content"));
                    p.setSummary(rs.getString("summary"));
                    p.setImage(rs.getString("image"));
                    p.setCategory(rs.getString("category"));
                    p.setTime_to_read(rs.getInt("time_to_read"));
                    p.setIsPublish(rs.getBoolean("isPublish"));
                    p.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                    p.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
                    p.setUser(user);
                    posts.add(p);
                }
                return posts;
            }, userId);
        } catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        }
    }

    @Override
    public void saveComment(Comment comment) {
        String query = """
                INSERT INTO comments (
                    message,
                    post_id,
                    user_id
                ) VALUES (?, ?, ?)
                """;
        log.info("Executing SQL query: {}", query);
        log.debug("Param userId: {} {} {}", comment.getMessage(), comment.getPostId(), comment.getUserId());
        jdbc.update(query, comment.getMessage(), comment.getPostId(), comment.getUserId());
    }
}
