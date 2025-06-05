package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.ReviewDTO;

import java.util.UUID;

public interface ReviewService {
    ReviewDTO viewReviewDetailOfUser(UUID uuid);
    CustomPage<ReviewDTO> viewReviews(UUID uuid, String orderBy);
    void createOrUpdateReview(ReviewDTO reviewDTO) throws Exception;
    CustomPage<ReviewDTO> viewReviews(String search, int page, int limit, String star);
    ReviewDTO viewReviewDetailOfAdmin(UUID uuid);
}
