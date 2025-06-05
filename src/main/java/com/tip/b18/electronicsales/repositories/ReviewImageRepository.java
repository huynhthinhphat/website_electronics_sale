package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, UUID> {
    List<ReviewImage> findAllByReviewId(UUID uuid);
    Set<ReviewImage> findAllByReviewIdIn(List<UUID> uuid);
}
