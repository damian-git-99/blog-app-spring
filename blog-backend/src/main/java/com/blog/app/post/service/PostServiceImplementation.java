package com.blog.app.post.service;

import com.blog.app.config.security.authentication.JWTAuthentication;
import com.blog.app.post.dao.PostDao;
import com.blog.app.post.exceptions.PostNotFoundException;
import com.blog.app.post.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class PostServiceImplementation implements PostService {

    private final PostDao postDao;

    @Autowired
    public PostServiceImplementation(PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    public boolean createPost(Post post) {
        JWTAuthentication authenticatedUser = getAuthenticatedUser();
        post.setUserId(authenticatedUser.getUserId());
        post.setCreated_at(new Date());
        post.setUpdated_at(new Date());
        // todo: image upload
        return postDao.savePost(post);
    }

    @Override
    public boolean editPost(Post post) {
        return false;
    }

    @Override
    public boolean deletePostById(Long id) {
        Optional<Post> optionalPost = postDao.getPostById(id);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not found");
        }
        if (!isPostOwnedByAuthenticatedUser(optionalPost.get())) {
            throw new PostNotFoundException("Post not found");
        }
        return postDao.deletePostById(id);
    }

    @Override
    public List<Post> getRecentlyPublishedPosts() {
        return postDao.getRecentlyPublishedPosts();
    }

    @Override
    public List<Post> getPostsOfAuthenticatedUser(Long userId) {
        JWTAuthentication authenticatedUser = getAuthenticatedUser();
        postDao.getPostsByUserId(authenticatedUser.getUserId());
        return postDao.getPostsByUserId(authenticatedUser.getUserId());
    }

    @Override
    public List<Post> getPostsByUsername(String username) {
        JWTAuthentication authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getUsername().equals(username)) {
            return postDao.getAllPostsByUsername(username);
        }

        return postDao.getPublicPostsByUsername(username);
    }

    @Override
    public Optional<Post> getPostById(Long postId) {
        Optional<Post> optionalPost = postDao.getPostById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not found");
        }
        Post post = optionalPost.get();

        if (!post.isPublish() && !isPostOwnedByAuthenticatedUser(post)) {
            throw new PostNotFoundException("Post not found");
        }

        return Optional.of(post);
    }

    @Override
    public boolean togglePublicationStatus(Long postId) {
        Optional<Post> optionalPost = this.postDao.getPostById(postId);

        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not found");
        }

        if (!isPostOwnedByAuthenticatedUser(optionalPost.get())) {
            throw new PostNotFoundException("Post not found");
        }

        return false;
    }

    /**
     * Checks if a post belongs to the authenticated user.
     *
     * @param post The post to be checked.
     * @return true if the post belongs to the authenticated user, false otherwise.
     */
    private boolean isPostOwnedByAuthenticatedUser(Post post) {
        try {
            log.info("Checking if post belongs to authenticated user");
            JWTAuthentication auth = getAuthenticatedUser();
            return Objects.equals(auth.getUserId(), post.getUserId());
        } catch (Exception e) {
            log.error("Error checking if post belongs to authenticated user");
            return false;
        }
    }

    /**
     * Obtains the authenticated user from the security context.
     *
     * @return The authenticated user's JWTAuthentication object.
     * @throws RuntimeException if the user is not authenticated.
     */
    private JWTAuthentication getAuthenticatedUser() {
        log.info("Getting authenticated user");
        JWTAuthentication principal = (JWTAuthentication) SecurityContextHolder
                .getContext().getAuthentication();

        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        return principal;
    }

}
