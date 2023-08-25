package com.blog.app.common.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile file);

    void deleteImage(String imageId);

    String getImageUrl(String imageId);
}
