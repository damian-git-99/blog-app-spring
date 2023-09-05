package com.blog.app.post.controllers;

import com.blog.app.post.model.Post;
import com.blog.app.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.blog.app.common.CommonUtils.handleValidationExceptions;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<?> createPost(
            @RequestBody @Valid Post post,
            BindingResult br, @RequestParam("file") MultipartFile image
    ) {
        if (br.hasErrors()) {
            return handleValidationExceptions(br);
        }
        postService.createPost(post, image);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void editPost(
            @PathVariable Long id,
            @RequestBody Post post,
            @RequestParam("file") MultipartFile image
    ) {
        post.setId(id);
        postService.editPost(post, image);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    List<Post> getRecentlyPosts() {
        return postService.getRecentlyPublishedPosts();
    }

    @GetMapping("/my-posts")
    @ResponseStatus(HttpStatus.OK)
    List<Post> getPostsOfAuthenticatedUser() {
        return postService.getPostsOfAuthenticatedUser();
    }

    @GetMapping("/by-username/{username}")
    @ResponseStatus(HttpStatus.OK)
    List<Post> getAllPostsByUsername(@PathVariable String username) {
        return postService.getPostsByUsername(username);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    Post getPostById(@PathVariable("postId") Long postId) {
        return postService.getPostById(postId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    void deletePostById(@PathVariable("postId") Long postId) {
        postService.deletePostById(postId);
    }

    @PutMapping("/toggle-status/{postId}")
    @ResponseStatus(HttpStatus.OK)
    void togglePublicationStatus(@PathVariable Long postId) {
        postService.togglePublicationStatus(postId);
    }

}
