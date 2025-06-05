package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ImageService {
    void addImages(List<String> images, Product product);
    List<String> getImagesByProductId(UUID productId);
    void deleteImagesByProductId(UUID productId, List<String> urls);
    void updateImagesByProductId(Product product, List<String> images);
    void updateImagesInDB(Product product, Set<String> imagesSet, Set<String> imageListSet);
    void deleteImagesOnCloudinary(List<String> imageList);
    String uploadImage(MultipartFile file);
    void deleteImages(List<String> images) throws Exception;
}
