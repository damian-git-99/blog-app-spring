package com.blog.app.config.security.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile file);

    void deleteImage(String imageId);

    String getImageUrl(String imageId);
}
