package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.entities.Cart;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.entities.OrderDetail;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.enums.Delivery;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.exceptions.IllegalStateException;
import com.tip.b18.electronicsales.exceptions.PayOSException;
import com.tip.b18.electronicsales.mappers.OrderMapper;
import com.tip.b18.electronicsales.mappers.TupleMapper;
import com.tip.b18.electronicsales.repositories.OrderRepository;
import com.tip.b18.electronicsales.services.*;
import com.tip.b18.electronicsales.utils.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final OrderMapper orderMapper;
    private final TupleMapper tupleMapper;
    private final @Lazy AccountService accountService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartItemService cartItemService;

    @Override
    public CustomPage<OrderDTO> viewOrders(String search, int page, int limit, Status status, PaymentMethod paymentMethod, Delivery delivery) {
        Pageable pageable = SecurityUtil.isAdminRole() ? PageRequest.of(page, limit) : Pageable.unpaged();
        UUID accountId = SecurityUtil.isAdminRole() ? null : SecurityUtil.getAuthenticatedUserId();

        Page<Order> orderList = orderRepository.findAllByConditions(
                search, accountId, status, paymentMethod, delivery, pageable);

        PageInfoDTO pageInfoDTO = new PageInfoDTO(orderList.getTotalElements(), orderList.getTotalPages());

        List<UUID> uuidList = SecurityUtil.isAdminRole() ? null : orderList.stream().map(Order::getId).toList();

        return SecurityUtil.isAdminRole()
                ? new CustomPage<>(orderMapper.toOrderDTOListByAdmin(orderList), pageInfoDTO)
                : new CustomPage<>(orderMapper.toOrderDTOListByUser(orderList, orderDetailService.findAllByOrderId(uuidList)), pageInfoDTO);
    }

    @Override
    public OrderDTO viewOrderDetails(UUID id) {
        Optional<Order> order = SecurityUtil.isAdminRole()
                ? orderRepository.findById(id)
                : orderRepository.findByIdAndAccountId(id, SecurityUtil.getAuthenticatedUserId());

        if(order.isEmpty()){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCT);
        }

        return orderMapper.toOrderDTO(order.get(), orderDetailService.findAllByOrderId(List.of(id)));
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        UUID accountId = SecurityUtil.getAuthenticatedUserId();
        String orderCode = orderDTO.getOrderCode() == null ? OrderUtil.generateOrderCode() : orderDTO.getOrderCode();

        System.out.println("orderDTO.getItems(): " + orderDTO.getItems().toString());
        productService.updateStockProducts(orderDTO.getItems(), false);

        Order order = orderRepository.save(
                orderMapper.toOrder(
                        orderDTO,
                        orderCode,
                        accountService.findById(accountId),
                        orderDTO.getStatus()));

        List<OrderDetailDTO> orderDetailDTOList = orderDetailService.createOrderDetails(order, orderDTO.getItems());

        if(orderDTO.isFromCart()){
            Cart cart = cartService.findByAccountId(accountId);
            if(cart == null){
                throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_CART);
            }
            List<UUID> cartItemIds = cartItemService.getCartItemsToDelete(cart, orderDTO.getItems());
            cartItemService.deleteItemsInCart(cartItemIds);
            cartService.updateTotalPriceAndTotalQuantityOfCart(cart);
        }
        return orderMapper.createOrderResponse(order, orderDetailDTOList, cartService.getTotalQuantityItemInCartByAccountId(accountId));
    }

    @Override
    @Transactional
    public void updateOrder(OrderDTO orderDTO) {
        boolean isAdmin = SecurityUtil.isAdminRole();
        Optional<Order> optionalOrder = isAdmin
                ? orderRepository.findById(orderDTO.getId())
                : orderRepository.findByIdAndAccountId(orderDTO.getId(), SecurityUtil.getAuthenticatedUserId());

        Order order = optionalOrder.orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_ORDER));

        if(order.getStatus().equals(Status.COMPLETED) || order.getStatus().equals(Status.CANCELED)){
            throw new IllegalStateException(String.format(MessageConstant.ERROR_UPDATE_ORDER, order.getStatus().getDisplayName()));
        }

        if(isAdmin){
            if(CompareUtil.compare(orderDTO.getStatus(), order.getStatus())){
                return;
            }
            order.setStatus(orderDTO.getStatus());
        }else{
            if(!order.getStatus().equals(Status.PENDING) && !order.getStatus().equals(Status.WAITING_FOR_PAYMENT)){
                throw new IllegalStateException(String.format(MessageConstant.ERROR_UPDATE_ORDER, order.getStatus().getDisplayName()));
            }
            if(!CompareUtil.compare(orderDTO.getFullName(), order.getFullName())){
                order.setFullName(orderDTO.getFullName());
            }

            if(!CompareUtil.compare(orderDTO.getPhoneNumber(), order.getPhoneNumber())){
                order.setPhoneNumber(orderDTO.getPhoneNumber());
            }

            if(!CompareUtil.compare(orderDTO.getAddress(), order.getAddress())){
                order.setAddress(orderDTO.getAddress());
            }

            if(!CompareUtil.compare(orderDTO.getStatus(), order.getStatus())){
                order.setStatus(Status.CANCELED);
            }
            order.setPaymentDeadline(null);
            order.setNote(orderDTO.getNote());
        }

        Order orderUpdate = orderRepository.save(order);
        if(!orderUpdate.getStatus().equals(Status.CANCELED)){
            return;
        }
        UUID orderId = orderUpdate.getId();
        List<OrderDetailDTO> orderDetails = orderDetailService.findAllByOrderId(orderId);
        productService.updateStockProducts(orderDetails, true);
    }

    @Override
    public int getQuantityNewOrders(LocalDateTime startDay, LocalDateTime endDay) {
        return orderRepository.countQuantityNewOrders(startDay, endDay, Status.CANCELED);
    }

    @Override
    public CustomList<ProductDTO> getTopProducts(int limit, String startDay, String endDay) {
        return new CustomList<>(
                tupleMapper.toProductDTOList(
                        orderRepository.getTopProducts(
                                PageableUtil.toPageable(limit),
                                LocalDateTimeUtil.parseStartDay(startDay),
                                LocalDateTimeUtil.parseEndDay(endDay),
                                Status.COMPLETED)
                )
        );
    }

    @Override
    public CustomList<DailyRevenueDTO> getDailyRevenue(int limit, String startDay, String endDay) {
        return new CustomList<>(
                tupleMapper.toDailyRevenueDTO(
                        orderRepository.getDailyRevenue(
                                PageableUtil.toPageable(limit),
                                LocalDateTimeUtil.parseStartDay(startDay),
                                LocalDateTimeUtil.parseEndDay(endDay))
                )
        );
    }

    @Override
    public void updateStatusOrder(Map<String, Object> payload) {
        if (payload == null) {
            throw new PayOSException(MessageConstant.INVALID_PAYLOAD);
        }
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        if (data == null) {
            throw new PayOSException(MessageConstant.INVALID_DATA_OF_PAYLOAD);
        }

        Object orderCodeObj = data.get("orderCode");
        if (orderCodeObj == null) {
            throw new PayOSException(MessageConstant.MISSING_ORDER_CODE);
        }

        long orderCode;
        try {
            orderCode = Long.parseLong(orderCodeObj.toString());
        } catch (NumberFormatException e) {
            throw new PayOSException(MessageConstant.INVALID_ORDER_CODE_OF_DATA);
        }

        Order order = orderRepository.findByOrderCodeAndStatus(String.valueOf(orderCode), Status.WAITING_FOR_PAYMENT);
        if(order != null){
            order.setStatus(Status.PENDING);
            orderRepository.save(order);
        }
    }

    @Override
    public void scheduleOrderStatusCheck() {
        List<Order> orderList = orderRepository.findByStatus(Status.WAITING_FOR_PAYMENT);

        if (!orderList.isEmpty()) {
            Map<UUID, Product> productMap = new HashMap<>();
            List<Order> ordersToUpdate = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (Order order : orderList) {
                productService.updateStockProducts(productMap, order.getOrderDetails());
                if (isPaymentDeadlinePassed(order, now)) {
                    order.setStatus(Status.CANCELED);
                    order.setPaymentDeadline(null);

                    ordersToUpdate.add(order);
                }
            }

            productService.saveAll(new ArrayList<>(productMap.values()));
            orderRepository.saveAll(ordersToUpdate);
        }
    }

    @Override
    public boolean isPaymentDeadlinePassed(Order order, LocalDateTime now) {
        Duration duration = Duration.between(order.getPaymentDeadline(), now);
        return duration.toMinutes() > (long) 15;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleOrderStatusCheckAt0Hour() {
        scheduleOrderStatusCheck();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 12 * * *")
    public void scheduleOrderStatusCheckAt12Hour() {
        scheduleOrderStatusCheck();
    }
}
