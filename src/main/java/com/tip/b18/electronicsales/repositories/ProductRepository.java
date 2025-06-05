package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Product;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "(SELECT AVG(r.star) FROM Review r LEFT JOIN OrderDetail o ON r.orderDetail.id = o.id WHERE o.product.id = p.id) AS star, " +
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
    List<Product> findAllByIdInAndIsDeleted(List<UUID> uuidList, boolean isDeleted);
    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdAt > :startDay AND p.createdAt < :endDay AND p.isDeleted = false")
    int countQuantityNewProducts(LocalDateTime startDay, LocalDateTime endDay);
    Page<Product> findAllByIsDeleted(boolean isDeleted, Pageable pageable);
    @Query("SELECT p AS product, " +
            "(SELECT COUNT(od) FROM OrderDetail od WHERE p.id = od.product.id) AS quantity " +
            "FROM Product p WHERE (p.deletedAt >= :startDay AND p.createdAt <= :endDay) AND p.isDeleted = true")
    List<Tuple> findAllByIsDeleted(@Param("startDay") LocalDateTime startDay,@Param("endDay") LocalDateTime endDay);
}
