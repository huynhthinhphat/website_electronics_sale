package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Color;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.entities.ProductColor;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, UUID> {
    @Query("SELECT pc AS productColor, " +
            "(SELECT COUNT(pcCount.color) " +
            "FROM ProductColor pcCount " +
            "WHERE pcCount.color = pc.color " +
            "GROUP BY pcCount.color) AS quantity " +
            "FROM ProductColor pc " +
            "WHERE pc.product.id IN :productIdList")
    List<Tuple> findAllByProductIds(@Param("productIdList") List<UUID> productIdList);
    List<ProductColor> findAllByProductIdIn(List<UUID> productIdList);
    ProductColor findByProductIdAndColorId(UUID productId, UUID colorId);
}
