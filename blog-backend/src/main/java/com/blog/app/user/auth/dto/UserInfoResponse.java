package com.blog.app.user.auth.dto;

import com.blog.app.user.model.User;
import lombok.Data;

@Data
public class UserInfoResponse {
    private String email;
    private String username;
    private Long id;

    public UserInfoResponse(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.id = user.getId();
    }
}
