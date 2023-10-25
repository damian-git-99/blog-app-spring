package com.blog.app.user.controllers;

import com.blog.app.post.dto.PostMapper;
import com.blog.app.post.dto.PostResponseDTO;
import com.blog.app.post.service.PostService;
import com.blog.app.user.dto.UserInfoResponseDTO;
import com.blog.app.user.dto.UserMapper;
import com.blog.app.user.model.User;
import com.blog.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PostService postService; // todo: mover ?

    @Autowired
    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/profile")
    public UserInfoResponseDTO userInfoAuthenticated() {
        return UserMapper
                .INSTANCE
                .toUserInfoResponseDTO(userService.getAuthenticatedUserInfo());
    }

    @PutMapping("/profile/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editProfile(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.editUser(user);
    }

    @PostMapping("/add-favorite-post/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void addFavoritePost(@PathVariable Long id) {
        userService.addFavoritePost(id);
    }


    @DeleteMapping("/delete-favorite-post/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFavoritePost(@PathVariable Long id) {
        userService.removeFavoritePost(id);
    }


    @GetMapping("/is-favorite-post/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> isPostMarkedAsFavorite(@PathVariable Long id) {
        boolean result = userService.isPostMarkedAsFavorite(id);
        return Map.of(
                "isMarked", result
        );
    }

    @GetMapping("/favorite-posts")
    public List<PostResponseDTO> getFavoritePosts() {
        return postService.getFavoritePostsByAuthenticatedUser()
                .stream()
                .map(PostMapper.INSTANCE::toPostResponseDTO)
                .toList();
    }

}
