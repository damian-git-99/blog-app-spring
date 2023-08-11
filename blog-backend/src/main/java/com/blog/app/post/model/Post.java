package com.blog.app.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String image;
    private String category;
    private int time_to_read;
    private boolean isPublish;
    // todo: add created_at and updated_at
}
