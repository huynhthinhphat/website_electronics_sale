package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = imageService.uploadImage(file);
        return ResponseEntity.ok(Collections.singletonMap("imageUrl", imageUrl));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestBody List<String> images) throws Exception {
        imageService.deleteImages(images);
        return ResponseEntity.noContent().build();
    }
}
