package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.OrderDTO;
import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.enums.PaymentMethod;
import com.tip.b18.electronicsales.enums.Status;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    default List<OrderDTO> toOrderDTOListByAdmin(Page<Order> orders){
        return orders
                .getContent()
                .stream()
                .map(order -> OrderDTO.builder()
                        .id(order.getId())
                        .orderCode(order.getOrderCode())
                        .phoneNumber(order.getAccount().getPhoneNumber())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus())
                        .paymentMethod(order.getPaymentMethod())
                        .delivery(order.getDelivery())
                        .build())
                .collect(Collectors.toList());
    }

    default List<OrderDTO> toOrderDTOListByUser(Page<Order> orders, List<OrderDetailDTO> detailDTOList){
        return orders
                .getContent()
                .stream()
                .map(order ->
                {
                    List<OrderDetailDTO> orderDetails = detailDTOList.stream()
                            .filter(orderDetail -> order.getId().equals(orderDetail.getOrderId()))
                            .peek(orderDetail -> orderDetail.setOrderId(null))
                            .toList();
                    return OrderDTO.builder()
                            .id(order.getId())
                            .orderCode(order.getOrderCode())
                            .totalPrice(order.getTotalPrice())
                            .status(order.getStatus())
                            .paymentMethod(order.getPaymentMethod())
                            .delivery(order.getDelivery())
                            .items(orderDetails)
                            .build();
                }).collect(Collectors.toList());
    }

    default OrderDTO createOrderResponse(Order order, List<OrderDetailDTO> detailDTOList, int totalQuantity){
        return OrderDTO
                .builder()
                .id(order.getId())
                .totalQuantity(totalQuantity)
                .orderCode(order.getOrderCode())
                .totalPrice(order.getTotalPrice())
                .items(detailDTOList)
                .build();
    }

    default OrderDTO toOrderDTO(Order order, List<OrderDetailDTO> detailDTOList){
        List<OrderDetailDTO> orderDetails = detailDTOList.stream()
                .peek(orderDetail -> orderDetail.setOrderId(null))
                .toList();
        OrderDTO.OrderDTOBuilder builder = OrderDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .delivery(order.getDelivery())
                .totalPrice(order.getTotalPrice())
                .items(orderDetails);
        if(!SecurityUtil.isAdminRole()){
            builder.fullName(order.getFullName());
        }
        return builder.build();
    }

    default Order toOrder(OrderDTO orderDTO, String orderCode, Account account, Status status){
        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setAccount(account);
        order.setFullName(orderDTO.getFullName());
        order.setPhoneNumber(orderDTO.getPhoneNumber());
        order.setAddress(orderDTO.getAddress());
        order.setFeeDelivery(orderDTO.getFeeDelivery());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setDelivery(orderDTO.getDelivery());
        order.setNote(orderDTO.getNote());
        order.setFromEstimateDate(orderDTO.getFromEstimateDate());
        order.setToEstimateDate(orderDTO.getToEstimateDate());
        if(status.equals(Status.PENDING) || status.equals(Status.WAITING_FOR_PAYMENT)){
            order.setStatus(status);
        }
        if(orderDTO.getPaymentMethod().equals(PaymentMethod.MOMO)){
            order.setPaymentDeadline(LocalDateTime.now().plusMinutes(10));
        }
        return order;
    }
}
