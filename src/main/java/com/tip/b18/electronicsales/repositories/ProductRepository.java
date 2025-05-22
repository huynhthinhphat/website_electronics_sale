package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Product;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p AS product, " +
            "(SELECT SUM(o.quantity) FROM OrderDetail o WHERE o.product.id = p.id) AS quantitySold, " +
            "i.url AS image, " +
            "c.color AS color " +
            "FROM Product p " +
            "LEFT JOIN Image i ON i.product.id = p.id " +
            "LEFT JOIN ProductColor pc ON pc.product.id = p.id " +
            "LEFT JOIN Color c ON c.id = pc.color.id " +
            "WHERE p.id = :id AND p.isDeleted = false")
    List<Tuple> findProductById(@Param("id") UUID id);
    Product findByIdAndIsDeleted(UUID id, boolean isDeleted);
    boolean existsBySkuAndIsDeleted(String sku, boolean isDeleted);
    List<Product> findByIsDeletedTrueAndDeletedAtBefore(LocalDateTime time);
    List<Product> findAllByIdInAndIsDeleted(List<UUID> uuidList, boolean isDeleted);
    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdAt > :startDay AND p.createdAt < :endDay AND p.isDeleted = false")
    int countQuantityNewProducts(LocalDateTime startDay, LocalDateTime endDay);
}
