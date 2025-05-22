package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.CartItemDTO;
import com.tip.b18.electronicsales.entities.CartItem;
import org.mapstruct.Mapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    default List<CartItemDTO> toCartItemList(List<CartItem> cartItemList, Map<UUID, List<String>> cartItemColors ){
        return cartItemList
                .stream()
                .filter(cartItem -> cartItemColors.containsKey(cartItem.getProduct().getId()))
                .map(cartItem -> CartItemDTO
                        .builder()
                        .id(cartItem.getId())
                        .productId(cartItem.getProduct().getId())
                        .sku(cartItem.getProduct().getSku())
                        .name(cartItem.getProduct().getName())
                        .color(cartItem.getColor())
                        .price(cartItem.getProduct().getDiscountPrice())
                        .quantity(cartItem.getQuantity())
                        .stock(cartItem.getProduct().getStock())
                        .totalPrice(cartItem.getTotalPrice())
                        .mainImageUrl(cartItem.getProduct().getMainImageUrl())
                        .isDeleted(cartItem.getProduct().isDeleted())
                        .colors(cartItemColors.get(cartItem.getProduct().getId()))
                        .build()
                )
                .toList();
    }
}
