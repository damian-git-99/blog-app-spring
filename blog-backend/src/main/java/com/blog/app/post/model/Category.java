package com.blog.app.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private String category;
    private Long postId;

    public Category(String category) {
        this.category = category;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
