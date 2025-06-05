package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.ResponseDTO;
import com.tip.b18.electronicsales.dto.ReviewDTO;
import com.tip.b18.electronicsales.services.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseDTO<CustomPage<ReviewDTO>> viewReviews(@RequestParam("id") UUID uuid,
                                                          @RequestParam(value = "orderBy", defaultValue = "") String orderBy){
        return new ResponseDTO<>("success", null, reviewService.viewReviews(uuid, orderBy));
    }

    @GetMapping("/user/detail")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseDTO<ReviewDTO> viewReviewDetailOfUser(@RequestParam("id") UUID uuid){
        return new ResponseDTO<>("success", null, reviewService.viewReviewDetailOfUser(uuid));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<CustomPage<ReviewDTO>> viewReviews(@RequestParam(name = "search", defaultValue = "") String search,
                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                          @RequestParam(name = "limit", defaultValue = "6") int limit,
                                                          @RequestParam(value = "star", defaultValue = "") String star){
        return new ResponseDTO<>("success", null, reviewService.viewReviews(search, page, limit, star));
    }

    @GetMapping("/admin/detail")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<ReviewDTO> viewReviewDetailOfAdmin(@RequestParam("id") UUID uuid){
        return new ResponseDTO<>("success", null, reviewService.viewReviewDetailOfAdmin(uuid));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseDTO<?> createOrUpdateReview(@RequestBody ReviewDTO reviewDTO) throws Exception {
        reviewService.createOrUpdateReview(reviewDTO);
        return new ResponseDTO<>("success", null, null);
    }
}
