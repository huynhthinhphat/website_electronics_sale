package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.ReviewDTO;
import com.tip.b18.electronicsales.entities.Review;
import com.tip.b18.electronicsales.entities.ReviewImage;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    default List<ReviewDTO> toReviewDTOList(Page<Review> reviews, Set<ReviewImage> reviewImages){
        HashMap<UUID, List<String>> imageMap = reviewImages.stream()
                .collect(Collectors.groupingBy(
                        ri -> ri.getReview().getId(),
                        HashMap::new,
                        Collectors.mapping(ReviewImage::getUrl, Collectors.toList())
                ));
        List<ReviewDTO> reviewDTOS = new ArrayList<>();
        for(Review review : reviews){
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setAvatar(review.getAccount().getAvatarUrl());
            reviewDTO.setFullName(review.getAccount().getFullName());
            reviewDTO.setProductName(review.getProduct().getName());
            reviewDTO.setColor(review.getOrderDetail().getColor());
            reviewDTO.setDescription(review.getDescription());
            reviewDTO.setStar(review.getStar());
            reviewDTO.setCreatedAt(review.getCreatedAt());
            List<String> images = imageMap.getOrDefault(review.getId(), new ArrayList<>());
            reviewDTO.setImages(images);
            reviewDTOS.add(reviewDTO);
        }
        return reviewDTOS;
    }

    default List<ReviewDTO> toReviewDTOList(Page<Review> reviews){
        List<ReviewDTO> reviewDTOS = new ArrayList<>();
        for(Review review : reviews){
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setId(review.getId());
            reviewDTO.setFullName(review.getAccount().getFullName());
            reviewDTO.setProductName(review.getProduct().getName());
            reviewDTO.setColor(review.getOrderDetail().getColor());
            reviewDTO.setDescription(review.getDescription());
            reviewDTO.setStar(review.getStar());
            reviewDTOS.add(reviewDTO);
        }
        return reviewDTOS;
    }

    default ReviewDTO toDTO(Review review, List<String> images, Double productStar){
        return ReviewDTO
                .builder()
                .fullName(review.getAccount().getFullName())
                .productName(review.getProduct().getName())
                .color(review.getOrderDetail().getColor())
                .description(review.getDescription())
                .createdAt(review.getCreatedAt())
                .images(images)
                .star(review.getStar())
                .mainImage(review.getProduct().getMainImageUrl())
                .productStar(productStar)
                .build();
    }
}
