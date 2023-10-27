package com.blog.app.post.model;

import com.blog.app.user.model.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private String category;
    @NotNull
    private Integer time_to_read;
    private boolean isPublish = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;
    private List<Comment> comments = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public boolean hasImage() {
        return image != null && !image.isEmpty();
    }

    public long getUserId() {
        return user.getId();
    }

    public void setIsPublish(boolean publish) {
        isPublish = publish;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void setCategories(String s) {
        // todo: remove this from a DTO
        String s1 = s.replace("[", "").replace("]", "");
        var list = Arrays.stream(s1.split(","))
                .map(Category::new)
                .toList();
        this.categories.addAll(list);
    }

}
