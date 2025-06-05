package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.PageInfoDTO;
import com.tip.b18.electronicsales.dto.ReviewDTO;
import com.tip.b18.electronicsales.entities.*;
import com.tip.b18.electronicsales.exceptions.InvalidValueException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.ReviewMapper;
import com.tip.b18.electronicsales.repositories.ReviewRepository;
import com.tip.b18.electronicsales.services.*;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final ReviewImageService reviewImageService;
    private final AccountService accountService;
    private final ProductService productService;

    @Override
    public ReviewDTO viewReviewDetailOfUser(UUID uuid) {
        OrderDetail orderDetail = orderDetailService.findOrderDetailById(uuid).orElseThrow(()-> new NotFoundException(MessageConstant.INVALID_ORDER_DETAIL));

        Order order = orderService.findByOrderId(orderDetail.getOrder().getId());

        if(!SecurityUtil.getAuthenticatedUserId().equals(order.getAccount().getId())){
            throw new NotFoundException(MessageConstant.INVALID_ORDER_DETAIL);
        }

        Review review = reviewRepository.findByOrderDetailId(uuid);
        ReviewDTO reviewDTO = new ReviewDTO();
        if(review == null) {
            reviewDTO.setProductName(orderDetail.getProduct().getName());
            reviewDTO.setColor(orderDetail.getColor());
            reviewDTO.setStar(-1);
        }else{
            reviewDTO.setProductName(review.getOrderDetail().getProduct().getName());
            reviewDTO.setColor(review.getOrderDetail().getColor());
            reviewDTO.setDescription(review.getDescription());
            reviewDTO.setStar(review.getStar());
            reviewDTO.setImages(reviewImageService.getImages(review.getId()));
        }
        return reviewDTO;
    }

    @Override
    public CustomPage<ReviewDTO> viewReviews(UUID uuid, String orderBy) {
        Page<Review> reviewList = orderBy.isEmpty() ? reviewRepository.findAllByProductId(uuid, null, Pageable.unpaged()) : reviewRepository.findAllByProductId(uuid, Integer.parseInt(orderBy), Pageable.unpaged());
        List<UUID> uuidList = reviewList.stream().map(Review::getId).toList();
        Set<ReviewImage> reviewImages = reviewImageService.getImagesByReviewIdList(uuidList);
        return new CustomPage<>(reviewMapper.toReviewDTOList(reviewList, reviewImages), new PageInfoDTO(reviewList.getTotalElements(), reviewList.getTotalPages()));
    }

    @Override
    public void createOrUpdateReview(ReviewDTO reviewDTO) throws Exception {
        OrderDetail orderDetail = orderDetailService.findOrderDetailById(reviewDTO.getOrderDetailId())
                .orElseThrow(() -> new InvalidValueException(MessageConstant.INVALID_ORDER_DETAIL));

        Review review = reviewRepository.findByOrderDetailId(orderDetail.getId());

        if(review == null){
            Account account = accountService.findById(SecurityUtil.getAuthenticatedUserId());
            Product product = productService.findProductById(orderDetail.getProduct().getId());

            review = new Review();
            review.setAccount(account);
            review.setProduct(product);
            review.setOrderDetail(orderDetail);
        }
        review.setDescription(reviewDTO.getDescription());
        review.setStar(reviewDTO.getStar());

        review = reviewRepository.save(review);
        reviewImageService.createOrDeleteImages(review, reviewDTO.getImages());
    }

    @Override
    public CustomPage<ReviewDTO> viewReviews(String search, int page, int limit, String star) {
        int minStar = 0;
        int maxStar = 5;
        if (star != null && !star.isBlank()) {
            String[] range = star.split("-");
            minStar = Integer.parseInt(range[0]);
            maxStar = Integer.parseInt(range[1]);
        }

        Pageable pageable = PageRequest.of(page, limit);
        Page<Review> reviews = reviewRepository.findAllByConditions(search, minStar, maxStar, pageable);

        return new CustomPage<>(reviewMapper.toReviewDTOList(reviews), new PageInfoDTO(reviews.getTotalElements(), reviews.getTotalPages()));
    }

    @Override
    public ReviewDTO viewReviewDetailOfAdmin(UUID uuid) {
        Review review = reviewRepository.findById(uuid).orElseThrow(()->new NotFoundException(MessageConstant.INVALID_REVIEW));
        return reviewMapper.toDTO(review, reviewImageService.getImages(review.getId()),reviewRepository.calculatorAvgStarByProductId(review.getProduct().getId()));
    }
}
