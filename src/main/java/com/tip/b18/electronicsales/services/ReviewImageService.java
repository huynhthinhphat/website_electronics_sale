package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.entities.Review;
import com.tip.b18.electronicsales.entities.ReviewImage;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReviewImageService {
    List<String> getImages(UUID uuid);
    Set<ReviewImage> getImagesByReviewIdList(List<UUID> uuidList);
    void createOrDeleteImages(Review review, List<String> images) throws Exception;
    void deleteImagesFromCloudinary(List<String> images) throws Exception;
    void deleteImagesFromDB(List<UUID> uuids);
}
