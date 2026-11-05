package com.expense.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.expense.entity.User;
import com.expense.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class ProfilePhotoController {

    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDirectory;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Please select a photo"));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Photo must be 5 MB or smaller"));
            }

            if (!ALLOWED_TYPES.contains(file.getContentType())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Only JPG, PNG and WEBP images are allowed"));
            }

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String extension = switch (file.getContentType()) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> throw new IllegalArgumentException("Unsupported image");
            };

            Path directory = Paths.get(uploadDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(directory);

            String filename = UUID.randomUUID() + extension;
            Path destination = directory.resolve(filename).normalize();

            if (!destination.startsWith(directory)) {
                return ResponseEntity.badRequest().build();
            }

            file.transferTo(destination);

            // Delete the previous uploaded profile photo, if present.
            String oldPicture = user.getPicture();

            if (oldPicture != null &&
                    oldPicture.startsWith("/uploads/")) {

                String oldFilename = oldPicture.substring(
                        oldPicture.lastIndexOf('/') + 1);

                Path oldFile = directory.resolve(oldFilename).normalize();

                if (oldFile.startsWith(directory)) {
                    Files.deleteIfExists(oldFile);
                }
            }

            String pictureUrl = "/uploads/" + filename;

            user.setPicture(pictureUrl);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Profile photo updated successfully",
                    "picture", pictureUrl
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unable to save profile photo"));
        }
    }
}