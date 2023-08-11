package com.blog.app.post.controllers;

import com.blog.app.post.model.Post;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @GetMapping("")
    List<Post> getRecentlyPosts() {
        Post post1 = new Post();
        post1.setTitle("Post 1");
        post1.setSummary("Summary 1");
        post1.setContent("Content 1");
        post1.setImage("Image 1");
        post1.setCategory("Category 1");
        return List.of(post1);
    }

    // todo: create post
    // todo: get recently posts
    // todo: get my posts
    // todo: delete post by id
    // todo: get post by id
    // todo: edit post
    // todo: togglePublicationStatus
    // todo: getPostsByUsername


}
