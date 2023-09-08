package com.blog.app.common.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class UploadImageCloudinaryService implements ImageService {

    private final Cloudinary cloudinary;

    @Autowired
    public UploadImageCloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("Unsupported file type. You must upload a PNG or JPG image.");
        }

        Map uploadResult = null;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String publicId = (String) uploadResult.get("public_id");
        System.out.println(uploadResult);
        return publicId;
    }

    @Override
    public void deleteImage(String imageId) {
        log.debug("Deleting image from cloudinary {} ", imageId);
        try {
            cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getImageUrl(String imageId) {
        log.debug("Getting image url from cloudinary {} ", imageId);
        return cloudinary.url().generate(imageId);
    }

}
