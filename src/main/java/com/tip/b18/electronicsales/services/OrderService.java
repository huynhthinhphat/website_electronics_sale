package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.DailyRevenueDTO;
import com.tip.b18.electronicsales.dto.OrderDTO;
import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
    CustomPage<OrderDTO> viewOrders(String search, int page, int limit, Status status, PaymentMethod paymentMethod, Delivery delivery);
    OrderDTO viewOrderDetails(UUID id);
    OrderDTO createOrder(OrderDTO orderDTO);
    void updateOrder(OrderDTO orderDTO);
    int getQuantityNewOrders(LocalDateTime startDay, LocalDateTime endDay);
    CustomList<DailyRevenueDTO> getDailyRevenue(int limit, String startDayReq, String endDayReq);
    CustomList<ProductDTO> getTopProducts(int limit, String startDay, String endDay);
    void updateStatusOrder(Map<String, Object> payload);
    boolean isPaymentDeadlinePassed(Order order, LocalDateTime now);
    void scheduleOrderStatusCheck();
    void scheduleOrderStatusCheckAt0Hour();
    void scheduleOrderStatusCheckAt12Hour();
}
