package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    @Query("SELECT i.url FROM Image i WHERE i.product.id = :id")
    List<String> findAllByProductId(UUID id);
    void deleteAllByProductIdAndUrlIn(UUID id, List<String> urls);
    void deleteAllByProductIdIn(List<UUID> ids);
}
