package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.entities.OrderDetail;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    default List<OrderDetailDTO> toOrderDetailDTOs(List<OrderDetail> orderDetails){
        return orderDetails
                .stream()
                .map(orderDetail -> {
                    OrderDetailDTO.OrderDetailDTOBuilder builder = OrderDetailDTO
                            .builder()
                            .sku(orderDetail.getProduct().getSku())
                            .name(orderDetail.getProduct().getName())
                            .quantity(orderDetail.getQuantity())
                            .priceAtTime(orderDetail.getPriceAtTime())
                            .totalPrice(orderDetail.getTotalPrice())
                            .color(orderDetail.getColor());

                    if(!SecurityUtil.isAdminRole()){
                        builder
                                .id(orderDetail.getProduct().getId())
                                .orderId(orderDetail.getOrder().getId())
                                .orderDetailId(orderDetail.getId())
                                .mainImageUrl(orderDetail.getProduct().getMainImageUrl());
                    }
                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    default OrderDetail toOrderDetail(Order order, OrderDetailDTO orderDetailDTO, Product product){
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setQuantity(orderDetailDTO.getQuantity());
        orderDetail.setPriceAtTime(orderDetailDTO.getPriceAtTime());
        orderDetail.setTotalPrice(orderDetailDTO.getTotalPrice());
        orderDetail.setProduct(product);
        return orderDetail;
    }

    default List<OrderDetailDTO> createOrderDetailResponse(List<OrderDetail> orderDetails){
        return orderDetails
                .stream()
                .map(orderDetail -> OrderDetailDTO
                        .builder()
                        .id(orderDetail.getId())
                        .sku(orderDetail.getProduct().getSku())
                        .name(orderDetail.getProduct().getName())
                        .quantity(orderDetail.getQuantity())
                        .price(orderDetail.getProduct().getDiscountPrice())
                        .totalPrice(orderDetail.getProduct().getDiscountPrice().multiply(new BigDecimal(orderDetail.getQuantity())))
                        .mainImageUrl(orderDetail.getProduct().getMainImageUrl())
                        .color(orderDetail.getColor())
                        .build())
                .toList();
    }
}
