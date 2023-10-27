package com.blog.app.post.model;

import com.blog.app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long id;
    private String message;
    private User user;
    private LocalDateTime createdAt;
    private Long postId;

    public Comment(String message, Long postId, User user) {
        this.message = message;
        this.user = user;
        this.postId = postId;
    }

    public Comment(String message, User user, LocalDateTime createdAt) {
        this.message = message;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return user.getId();
    }
}
