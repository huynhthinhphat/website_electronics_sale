package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.entities.OrderDetail;

import java.util.List;
import java.util.UUID;

public interface OrderDetailService {
    List<OrderDetailDTO> findAllByOrderId(List<UUID> uuidList);
    List<OrderDetailDTO> findAllByOrderId(UUID orderId);
    List<OrderDetailDTO> createOrderDetails(Order order, List<OrderDetailDTO> orderDetailDTOList);
}
