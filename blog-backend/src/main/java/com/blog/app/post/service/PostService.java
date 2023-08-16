package com.blog.app.post.service;

import com.blog.app.post.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    boolean createPost(Post post);

    boolean editPost(Post post);

    boolean deletePostById(Long id);

    List<Post> getRecentlyPublishedPosts();

    List<Post> getPostsByUserId(Long userId);

    List<Post> getPostsByUsername(String username);

    Optional<Post> getPostById(Long postId);

    boolean togglePublicationStatus(Long postId);

}
