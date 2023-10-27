package com.blog.app.post.service;

import com.blog.app.post.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    boolean createPost(Post post, MultipartFile image);

    boolean editPost(Post post, MultipartFile image);

    boolean deletePostById(Long id);

    List<Post> getRecentlyPublishedPosts();

    List<Post> getPostsOfAuthenticatedUser();

    List<Post> getPostsByUsername(String username);

    Post getPostById(Long postId);

    void togglePublicationStatus(Long postId);
    List<Post> getFavoritePostsByAuthenticatedUser();

    void createComment(String message, Long postId);

}
