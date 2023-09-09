package com.blog.app.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    public User(Long id) {
        this.id = id;
    }
}
