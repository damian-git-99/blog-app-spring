package com.blog.app.post.dto;

import com.blog.app.user.dto.UserInfoResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponseDTO {

    private Long id;
    private String title;
    private String summary;
    private String content;
    private String image;
    private String category;
    private int time_to_read;
    private boolean isPublish = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserInfoResponseDTO user;
    private List<CommentDTO> comments = new ArrayList<>();
    private List<CategoryDTO> categories = new ArrayList<>();

    @JsonProperty("isPublish")
    public boolean isPublish() {
        return isPublish;
    }

    public List<String> getCategories() {
        return categories.stream()
                .map(CategoryDTO::getCategory)
                .toList();
    }
}
