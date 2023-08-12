package com.blog.app.post.dao;

import com.blog.app.post.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    boolean savePost(Post post);

    boolean editPost(Post post);

    boolean deletePostById(Long id);

    List<Post> getRecentlyPublishedPosts();

    List<Post> getMyPostsById(Long userId);

    List<Post> getPostsByUsername(String username);

    Optional<Post> getPostById(Long postId);

    boolean togglePublicationStatus();
}
