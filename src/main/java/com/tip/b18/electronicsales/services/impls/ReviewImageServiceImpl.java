package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.entities.Review;
import com.tip.b18.electronicsales.entities.ReviewImage;
import com.tip.b18.electronicsales.entities.base.BaseIdEntity;
import com.tip.b18.electronicsales.repositories.ReviewImageRepository;
import com.tip.b18.electronicsales.services.CloudinaryService;
import com.tip.b18.electronicsales.services.ReviewImageService;
import com.tip.b18.electronicsales.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewImageServiceImpl implements ReviewImageService {
    private final ReviewImageRepository reviewImageRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<String> getImages(UUID uuid) {
        return reviewImageRepository.findAllByReviewId(uuid).stream().map(ReviewImage::getUrl).toList();
    }

    @Override
    public Set<ReviewImage> getImagesByReviewIdList(List<UUID> uuidList) {
        return reviewImageRepository.findAllByReviewIdIn(uuidList);
    }

    @Override
    public void createOrDeleteImages(Review review, List<String> images) throws Exception {
        List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewId(review.getId());
        List<String> imageList = reviewImages.stream().map(ReviewImage::getUrl).toList();

        List<String> imagesToSave = new ArrayList<>();
        if(images != null){
            imagesToSave = images.stream().filter(image -> !imageList.contains(image)).toList();
            List<String> imagesToDelete = imageList.stream().filter(image -> !images.contains(image)).toList();

            List<UUID> idsToDelete = reviewImages.stream().filter(item -> imagesToDelete.contains(item.getUrl())).map(BaseIdEntity::getId).toList();
            deleteImagesFromDB(idsToDelete);
            deleteImagesFromCloudinary(imagesToDelete);
        }

        List<ReviewImage> newReviewImageList = new ArrayList<>();
        for(String image : imagesToSave){
            ReviewImage reviewImage = new ReviewImage();
            reviewImage.setReview(review);
            reviewImage.setUrl(image);

            newReviewImageList.add(reviewImage);
        }
        reviewImageRepository.saveAll(newReviewImageList);
    }

    @Override
    @Async
    public void deleteImagesFromCloudinary(List<String> images) throws Exception {
        for(String image : images){
            cloudinaryService.deleteImage(ImageUtil.getPublicIdFromUrl(image));
        }
    }

    @Override
    @Async
    public void deleteImagesFromDB(List<UUID> uuids) {
        reviewImageRepository.deleteAllByIdInBatch(uuids);
    }
}
