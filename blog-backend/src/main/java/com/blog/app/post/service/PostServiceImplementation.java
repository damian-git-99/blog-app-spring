package com.blog.app.post.service;

import com.blog.app.post.dao.PostDao;
import com.blog.app.post.model.Post;
import com.blog.app.user.model.User;
import com.blog.app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class PostServiceImplementation implements PostService {

    private final PostDao postDao;
    private final UserService userService;

    @Autowired
    public PostServiceImplementation(PostDao postDao, UserService userService) {
        this.postDao = postDao;
        this.userService = userService;
    }

    @Override
    public boolean createPost(Post post) {
        // todo: set user id from authenticated user
        post.setUserId(1L);
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
        // todo: check if post belongs to the authenticated user
        if (!postBelongsToAuthenticatedUser(id)) {
            // todo: throw exception
        }
        return postDao.deletePostById(id);
    }

    @Override
    public List<Post> getRecentlyPublishedPosts() {
        return postDao.getRecentlyPublishedPosts();
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        // todo: return post of authenticated user
        return null;
    }

    @Override
    public List<Post> getPostsByUsername(String username) {
        return postDao.getPostsByUsername(username);
    }

    @Override
    public Optional<Post> getPostById(Long postId) {
        return postDao.getPostById(postId);
    }

    @Override
    public boolean togglePublicationStatus(Long postId) {
        // todo: check if post belongs to the authenticated user
        return false;
    }

    /**
     * Checks if a post belongs to the authenticated user.
     *
     * @param post The post to be checked.
     * @return true if the post belongs to the authenticated user, false otherwise.
     */
    private boolean postBelongsToAuthenticatedUser(Post post) {
        try {
            // todo : throw exception if user is not authenticated
            log.info("Checking if post belongs to authenticated user");
            Principal principal = SecurityContextHolder
                    .getContext().getAuthentication();

            String email = principal.getName();
            Optional<User> optionalUser = userService.findUserByEmail(email);

            if (optionalUser.isEmpty()) {
                return false;
            }

            User user = optionalUser.get();

            return Objects.equals(user.getId(), post.getUserId());
        } catch (Exception e) {
            log.error("Error checking if post belongs to authenticated user");
            return false;
        }
    }

    private boolean postBelongsToAuthenticatedUser(Long postId) {
        Optional<Post> optionalPost = getPostById(postId);
        if (optionalPost.isEmpty()) return false;
        return postBelongsToAuthenticatedUser(optionalPost.get());
    }

}
