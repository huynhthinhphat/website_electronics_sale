package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.CartItem;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findAllByCartId(UUID uuid);
    @Query("SELECT c FROM CartItem c WHERE LOWER(c.color) = LOWER(:color) AND c.cart.id = :cartId AND c.product.id = :productId")
    CartItem findByCartIdAndProductIdAndColor(@Param("cartId") UUID cartId,
                                              @Param("productId") UUID productId,
                                              @Param("color") String color);
    List<CartItem> findAllByIdIn(List<UUID> uuidList);
    @Query("SELECT SUM(ci.totalPrice) AS totalPrice, COUNT(ci) AS totalQuantity " +
            "FROM CartItem ci " +
            "WHERE ci.cart.id = :cartId")
    Tuple calculatorTotalPriceAndTotalQuantityOfCart(@Param("cartId") UUID cartId);
    List<CartItem> findAllByCartIdAndProductIdInAndColorIn(UUID cartId, List<UUID> productIds, List<String> color);
}
