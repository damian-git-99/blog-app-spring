package com.blog.app.post.dto;

import com.blog.app.user.dto.UserInfoResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private String message;
    private UserInfoResponseDTO user;
    private LocalDateTime createdAt;
}
