package com.blog.app.post.service;

import com.blog.app.config.security.AuthenticationUtils;
import com.blog.app.config.security.authentication.AuthenticatedUser;
import com.blog.app.common.image.ImageService;
import com.blog.app.post.dao.PostDao;
import com.blog.app.post.exceptions.PostNotFoundException;
import com.blog.app.post.model.Post;
import com.blog.app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.blog.app.common.CommonUtils.mergeNullableFields;

@Service
@Slf4j
public class PostServiceImplementation implements PostService {

    private final PostDao postDao;
    private final ImageService imageService;
    private final AuthenticationUtils authenticationUtils;

    @Autowired
    public PostServiceImplementation(
            PostDao postDao,
            ImageService imageService,
            AuthenticationUtils authenticationUtils
    ) {
        this.postDao = postDao;
        this.imageService = imageService;
        this.authenticationUtils = authenticationUtils;
    }

    @Override
    public boolean createPost(Post post, MultipartFile image) {
        log.info("Creating post");
        AuthenticatedUser authenticatedUser = authenticationUtils.getAuthenticatedUser();
        post.setUser(new User(authenticatedUser.getUserId()));
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        if (isImageNotEmpty(image)) {
            String imageId = imageService.uploadImage(image);
            post.setImage(imageId);
        }
        log.info("Post was created successfully");
        return postDao.savePost(post);
    }

    @Override
    public boolean editPost(Post post, MultipartFile image) {
        log.info("Editing post: " + post.getId());
        Post oldPost = getExistingPostById(post.getId());
        verifyPostOwnership(oldPost);
        post.setTitle(mergeNullableFields(oldPost.getTitle(), post.getTitle()));
        post.setSummary(oldPost.getSummary() == null ? post.getSummary() : oldPost.getSummary());
        post.setContent(oldPost.getContent() == null ? post.getContent() : oldPost.getSummary());
        post.setCategory(mergeNullableFields(oldPost.getCategory(), post.getCategory()));
        post.setTime_to_read(mergeNullableFields(oldPost.getTime_to_read(), post.getTime_to_read()));
        post.setUpdatedAt(LocalDateTime.now());
        if (isImageNotEmpty(image)) {
            String imageId = imageService.uploadImage(image);
            deleteImageIfPostHasImage(oldPost);
            post.setImage(imageId);
        }
        log.info("Post was edited successfully");
        return postDao.editPost(post);
    }


    @Override
    public boolean deletePostById(Long id) {
        log.info("Deleting post: " + id);
        Post post = getExistingPostById(id);
        verifyPostOwnership(post);
        deleteImageIfPostHasImage(post);
        return postDao.deletePostById(id);
    }

    @Override
    public List<Post> getRecentlyPublishedPosts() {
        log.info("Getting recently published posts");
        return updatePostImageUrls(postDao.getRecentlyPublishedPosts());
    }

    @Override
    public List<Post> getPostsOfAuthenticatedUser() {
        log.info("Getting posts of authenticated user");
        AuthenticatedUser authenticatedUser = authenticationUtils.getAuthenticatedUser();
        return updatePostImageUrls(postDao.getPostsByUserId(authenticatedUser.getUserId()));
    }

    @Override
    public List<Post> getPostsByUsername(String username) {
        log.info("Getting posts by username: " + username);
        if (isAuthenticatedUser(username)) {
            return updatePostImageUrls(postDao.getAllPostsByUsername(username));
        }
        return updatePostImageUrls(postDao.getPublicPostsByUsername(username));
    }

    @Override
    public Post getPostById(Long postId) {
        log.info("Getting post by id: " + postId);
        Post post = getExistingPostById(postId);
        validatePostAccess(post);
        return updatePostImageUrls(post);
    }

    @Override
    public void togglePublicationStatus(Long postId) {
        log.info("Toggling publication status of post: " + postId);
        Post post = getExistingPostById(postId);
        verifyPostOwnership(post);
    }


    /**
     * Updates the image to the full image URL in a Post object.
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

    /**
     * Updates the image name to the full image URL in a Post object.
     * This is necessary because the Post model stores only the image name, not the full URL.
     *
     * @param posts The Posts list to which the image URL will be updated.
     * @return The updated Posts list with the full image URL.
     */
    private List<Post> updatePostImageUrls(List<Post> posts) {
        return posts.stream()
                .map(this::updatePostImageUrls)
                .toList();
    }

    private Post getExistingPostById(Long postId) {
        Optional<Post> optionalPost = postDao.getPostById(postId);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException("Post not found: " + postId);
        }
        return optionalPost.get();
    }

    /**
     * Verifies if the authenticated user owns the given post.
     *
     * @param post The post to be verified.
     * @throws PostNotFoundException If the post doesn't belong to the authenticated user.
     */
    private void verifyPostOwnership(Post post) {
        if (!isPostOwnedByAuthenticatedUser(post)) {
            throw new PostNotFoundException("Post not found: " + post.getId());
        }
    }


    private void validatePostAccess(Post post) {
        if (!post.isPublish() && !isPostOwnedByAuthenticatedUser(post)) {
            throw new PostNotFoundException("Post not found: " + post.getId());
        }
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
            AuthenticatedUser auth = authenticationUtils.getAuthenticatedUser();
            return Objects.equals(auth.getUserId(), post.getUserId());
        } catch (Exception e) {
            log.error("Error checking if post belongs to authenticated user");
            return false;
        }
    }

    private boolean isImageEmpty(MultipartFile image) {
        return image == null || image.isEmpty();
    }

    private boolean isImageNotEmpty(MultipartFile image) {
        return !isImageEmpty(image);
    }

    /**
     * Deletes the associated image of a post if it exists.
     *
     * @param post The post for which the associated image will be deleted.
     */
    private void deleteImageIfPostHasImage(Post post) {
        if (post.hasImage()) {
            log.info("Deleting image of post: " + post.getId());
            imageService.deleteImage(post.getImage());
        }
    }

    /**
     * Verifies if the authenticated user matches the provided username.
     *
     * @param username The username for which authentication is being verified.
     * @return true if the authenticated user matches the provided username, false otherwise.
     */
    public boolean isAuthenticatedUser(String username) {
        if (!authenticationUtils.isUserAuthenticated()) {
            return false;
        }
        AuthenticatedUser authenticatedUser = authenticationUtils.getAuthenticatedUser();
        return authenticatedUser.getUsername().equals(username);
    }
}
