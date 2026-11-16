
package com.ecotrack.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${app.upload.dir}")
    private String uploadDirectory;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public String storeImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload an image.");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Image size must not exceed 5 MB.");
        }

        Path uploadPath = Paths.get(uploadDirectory)
                .toAbsolutePath().normalize();

        Files.createDirectories(uploadPath);

        String originalName = file.getOriginalFilename();
        String extension = ".jpg";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(
                    originalName.lastIndexOf(".")).toLowerCase();
        }

        String fileName = UUID.randomUUID() + extension;

        Path destination = uploadPath.resolve(fileName).normalize();

        if (!destination.getParent().equals(uploadPath)) {
            throw new IOException("Invalid file path.");
        }

        Files.copy(file.getInputStream(), destination,
                StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
