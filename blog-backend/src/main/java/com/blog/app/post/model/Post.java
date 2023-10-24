package com.blog.app.post.model;

import com.blog.app.user.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String summary;
    @NotEmpty
    private String content;
    private String image;
    @NotEmpty
    private String category;
    @NotNull
    private Integer time_to_read;
    private boolean isPublish = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;

    public boolean hasImage() {
        return image != null && !image.isEmpty();
    }

    public long getUserId() {
        return user.getId();
    }

    public void setIsPublish(boolean publish) {
        isPublish = publish;
    }
}
