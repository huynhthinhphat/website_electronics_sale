package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Review findByOrderDetailId(UUID uuid);
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND (:star IS NULL OR r.star = :star) ORDER BY r.createdAt DESC")
    Page<Review> findAllByProductId(@Param("productId") UUID uuid, @Param("star") Integer star, Pageable pageable);
    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE (:search IS NULL OR :search = '' OR LOWER(r.account.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR r.account.phoneNumber LIKE CONCAT('%', :search, '%') " +
            "OR LOWER(r.product.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (r.star >= :minStar AND r.star <= :maxStar) " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findAllByConditions(@Param("search") String search, @Param("minStar") Integer minStar, @Param("maxStar") Integer maxStar, Pageable pageable);
    @Query("SELECT AVG(r.star) FROM Review r WHERE r.product.id = :id")
    Double calculatorAvgStarByProductId(@Param("id") UUID uuid);
}
