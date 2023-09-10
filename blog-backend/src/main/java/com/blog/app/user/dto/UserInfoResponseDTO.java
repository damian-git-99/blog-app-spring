package com.blog.app.user.dto;

import lombok.Data;

@Data
public class UserInfoResponseDTO {
    private String email;
    private String username;
    private Long id;
}
