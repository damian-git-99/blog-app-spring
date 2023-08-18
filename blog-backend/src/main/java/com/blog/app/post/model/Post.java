package com.blog.app.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    // todo: add validations
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String image;
    private String category;
    private int time_to_read;
    private boolean isPublish;
    private Long userId;
    private Date created_at;
    private Date updated_at;
}
