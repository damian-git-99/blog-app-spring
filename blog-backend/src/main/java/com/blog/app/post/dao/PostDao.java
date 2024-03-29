package com.blog.app.post.dao;

import com.blog.app.post.model.Comment;
import com.blog.app.post.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostDao {
    boolean savePost(Post post);

    boolean editPost(Post post);

    boolean deletePostById(Long id);

    List<Post> getRecentlyPublishedPosts();

    List<Post> getPostsByUserId(Long userId);

    List<Post> getPublicPostsByUsername(String username);

    List<Post> getAllPostsByUsername(String username);

    Optional<Post> getPostById(Long postId);

    boolean togglePublicationStatus(Long postId);
    List<Post> getFavoritePostsByUserId(Long userId);

    void saveComment(Comment comment);
}
