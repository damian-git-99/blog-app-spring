package com.blog.app.common.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class UploadImageCloudinaryService implements ImageService {

    @Override
    public String uploadImage(MultipartFile file) {
        log.info("Uploading image to cloudinary");
        System.out.println("Uploading image to cloudinary");
        return "image.png";
    }

    @Override
    public void deleteImage(String imageId) {
        log.info("Deleting image from cloudinary");
    }

    @Override
    public String getImageUrl(String imageId) {
        System.out.println("Getting image url from cloudinary");
        return "https://images.unsplash.com/photo-1684577753340-de97c66fa6fd?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1032&q=80";
    }

}
