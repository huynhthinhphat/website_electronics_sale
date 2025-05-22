package com.tip.b18.electronicsales.repositories;

import com.tip.b18.electronicsales.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    List<OrderDetail> findAllByOrderIdIn(List<UUID> uuidList);
    List<OrderDetail> findAllByOrderId(UUID orderId);
}
