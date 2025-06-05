package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o " +
            "FROM Order o " +
            "WHERE (o.orderCode LIKE CONCAT('%', :search, '%') OR o.account.phoneNumber LIKE CONCAT('%', :search, '%')) " +
            "AND (:accountId IS NULL OR o.account.id = :accountId)" +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:notStatus IS NULL OR o.status <> :notStatus) " +
            "AND (:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod) " +
            "AND (:delivery IS NULL OR o.delivery = :delivery) " +
            "AND o.createdAt >= :startDay " +
            "AND o.createdAt <= :endDay " +
            "ORDER BY o.createdAt DESC")
    Page<Order> findAllByConditions(
            @Param("search") String search,
            @Param("accountId") UUID accountId,
            @Param("status") Status status,
            @Param("notStatus") Status notStatus,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("delivery") Delivery delivery,
            @Param("startDay") LocalDateTime startDay,
            @Param("endDay") LocalDateTime endDay,
            Pageable pageable);
    Optional<Order> findByIdAndAccountId(UUID id, UUID accountId);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt > :startDay AND o.createdAt < :endDay AND o.status <> :status")
    int countQuantityNewOrders(LocalDateTime startDay, LocalDateTime endDay, Status status);
    @Query("SELECT p.sku as productSku, p.name AS productName, SUM(od.quantity) AS quantitySold " +
            "FROM Order o " +
            "JOIN OrderDetail od ON od.order.id = o.id " +
            "JOIN Product p ON p.id = od.product.id " +
            "WHERE (:startDay IS NULL OR o.createdAt >= :startDay) AND o.createdAt <= :endDay AND o.status = :status " +
            "GROUP BY p.sku, p.name " +
            "ORDER BY quantitySold DESC")
    List<Tuple> getTopProducts(Pageable pageable,
                               @Param("startDay") LocalDateTime startDay,
                               @Param("endDay") LocalDateTime endDay,
                               @Param("status") Status status);
    @Query("SELECT SUM(o.totalPrice) AS totalPrice, " +
            "COUNT(o) AS totalOrder, " +
            "FUNCTION('DATE', o.createdAt) AS date " +
            "FROM Order o " +
            "WHERE (:startDay IS NULL OR o.createdAt >= :startDay) AND o.createdAt <= :endDay " +
            "GROUP BY FUNCTION('DATE', o.createdAt) " +
            "ORDER BY FUNCTION('DATE', o.createdAt) DESC")
    List<Tuple> getDailyRevenue(Pageable pageable, LocalDateTime startDay, LocalDateTime endDay);
    Order findByOrderCodeAndStatus(String orderCode, Status status);
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") Status status);
    @Query("SELECT o FROM Order o WHERE o.status = :status AND (o.toEstimateDate >= :startDay AND o.toEstimateDate <= :endDay)")
    List<Order> findByStatusAndDate(@Param("status") Status status, @Param("startDay") LocalDateTime startDay, @Param("endDay") LocalDateTime endDay);
}
