package com.blog.app.post.service;

import com.blog.app.config.security.authentication.JWTAuthentication;
import com.blog.app.config.security.image.ImageService;
import com.blog.app.post.dao.PostDao;
import com.blog.app.post.exceptions.PostNotFoundException;
import com.blog.app.post.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class PostServiceImplementation implements PostService {

    private final PostDao postDao;
    private final ImageService imageService;

    @Autowired
    public PostServiceImplementation(PostDao postDao, ImageService imageService) {
        this.postDao = postDao;
        this.imageService = imageService;
    }

    @Override
    public boolean createPost(Post post, MultipartFile image) {
        log.info("Creating post");
        JWTAuthentication authenticatedUser = getAuthenticatedUser();
        post.setUserId(authenticatedUser.getUserId());
        LocalDateTime now = LocalDateTime.now();
        post.setCreated_at(now);
        post.setUpdated_at(now);
        if (isImageNotEmpty(image)) {
            String imageId = imageService.uploadImage(image);
            post.setImage(imageId);
        }
        return postDao.savePost(post);
    }

    @Override
    public boolean editPost(Post post, MultipartFile image) {
        Optional<Post> optionalPost = postDao.getPostById(post.getId());
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not found");
        }
        Post oldPost = optionalPost.get();
        if (!isPostOwnedByAuthenticatedUser(post)) {
            throw new PostNotFoundException("Post not found");
        }
        post.setTitle(mergeNullableFields(oldPost.getTitle(), post.getTitle()));
        post.setSummary(oldPost.getSummary() == null ? post.getSummary() : oldPost.getSummary());
        post.setContent(oldPost.getContent() == null ? post.getContent() : oldPost.getSummary());
        post.setCategory(mergeNullableFields(oldPost.getCategory(), post.getCategory()));
        post.setTime_to_read(mergeNullableFields(oldPost.getTime_to_read(), post.getTime_to_read()));
        post.setUpdated_at(LocalDateTime.now());

        if (isImageNotEmpty(image)) {
            String imageId = imageService.uploadImage(image);
            if (oldPost.hasImage()) {
                imageService.deleteImage(oldPost.getImage());
            }
            post.setImage(imageId);
        }

        return postDao.editPost(post);
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

        Post post = optionalPost.get();

        if (post.hasImage()) {
            imageService.deleteImage(post.getImage());
        }

        return postDao.deletePostById(id);
    }

    @Override
    public List<Post> getRecentlyPublishedPosts() {
        return updatePostImageUrls(postDao.getRecentlyPublishedPosts());
    }

    @Override
    public List<Post> getPostsOfAuthenticatedUser(Long userId) {
        JWTAuthentication authenticatedUser = getAuthenticatedUser();
        postDao.getPostsByUserId(authenticatedUser.getUserId());
        return updatePostImageUrls(postDao.getPostsByUserId(authenticatedUser.getUserId()));
    }

    @Override
    public List<Post> getPostsByUsername(String username) {
        JWTAuthentication authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getUsername().equals(username)) {
            return postDao.getAllPostsByUsername(username);
        }

        return updatePostImageUrls(postDao.getPublicPostsByUsername(username));
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

        return Optional.of(updatePostImageUrls(post));
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

    /**
     * Merges two values, prioritizing the new value if it's not null.
     *
     * @param oldValue The original value.
     * @param newValue The new value.
     * @return The merged value, preferring newValue if it's not null, otherwise oldValue.
     */
    private String mergeNullableFields(String oldValue, String newValue) {
        return newValue == null ? oldValue : newValue;
    }

    /**
     * Merges two values, prioritizing the new value if it's not null.
     *
     * @param oldValue The original value.
     * @param newValue The new value.
     * @return The merged value, preferring newValue if it's not null, otherwise oldValue.
     */
    private int mergeNullableFields(int oldValue, int newValue) {
        return newValue == 0 ? oldValue : newValue;
    }

    /**
     * Updates the image name to the full image URL in a Post object.
     * This is necessary because the Post model stores only the image name, not the full URL.
     *
     * @param post The Post object to which the image URL will be updated.
     * @return The updated Post object with the full image URL.
     */
    private Post updatePostImageUrls(Post post) {
        if (post.getImage() != null && !post.getImage().isEmpty()) {
            post.setImage(imageService.getImageUrl(post.getImage()));
        }
        return post;
    }

    private List<Post> updatePostImageUrls(List<Post> posts) {
        return posts.stream()
                .map(this::updatePostImageUrls)
                .toList();
    }

    private boolean isImageEmpty(MultipartFile image) {
        return image == null || image.isEmpty();
    }

    private boolean isImageNotEmpty(MultipartFile image) {
        return !isImageEmpty(image);
    }
}
