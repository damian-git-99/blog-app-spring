package com.blog.app.post.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

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
    @NotEmpty
    private int time_to_read;
    private boolean isPublish = false;
    @NotEmpty
    private Long userId;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
