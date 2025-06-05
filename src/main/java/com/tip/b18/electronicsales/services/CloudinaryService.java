package com.tip.b18.electronicsales.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    void deleteImage(String publicId) throws Exception;
    Map<?,?> uploadImages(MultipartFile file) throws IOException;
}
