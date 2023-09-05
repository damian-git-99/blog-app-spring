package com.blog.app.user.controllers;

import com.blog.app.user.auth.dto.UserInfoResponse;
import com.blog.app.user.exceptions.UserNotFoundException;
import com.blog.app.user.model.User;
import com.blog.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserInfoResponse userInfoAuthenticated() {
        return userService.getAuthenticatedUserInfo()
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PutMapping("/profile/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editProfile(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.editUser(user);
    }

    @PutMapping("/add-favorite-post/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void addFavoritePost(@PathVariable Long id) {
        userService.addFavoritePost(id);
    }


    @PutMapping("/delete-favorite-post/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFavoritePost(@PathVariable Long id) {
        userService.removeFavoritePost(id);
    }


    @GetMapping("/is-post-marked-as-favorite/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> isPostMarkedAsFavorite(@PathVariable Long id) {
        boolean result = userService.isPostMarkedAsFavorite(id);
        return Map.of(
                "isMarked", result
        );
    }


}
