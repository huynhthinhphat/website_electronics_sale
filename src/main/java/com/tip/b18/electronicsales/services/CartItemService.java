package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.CartItemDTO;
import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.Cart;
import com.tip.b18.electronicsales.entities.CartItem;
import com.tip.b18.electronicsales.entities.Product;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CartItemService {
    List<CartItemDTO> findAllByCartId(UUID uuid);
    void addNewItemToCart(Cart cart, Product product, CartItemDTO cartItemDTO);
    boolean existCartItem(Cart cart, Product product, String color);
    List<CartItem> deleteItemsInCart(CustomList<UUID> uuidList);
    void updateQuantityOrColorItemsInCart(Cart cart, List<CartItemDTO> cartItemDTO);
    Tuple calculatorTotalPriceAndTotalQuantityOfCart(UUID cartId);
    List<UUID> getCartItemsToDelete(Cart cart, List<OrderDetailDTO> orderDetailDTOList);
    void deleteItemsInCart(List<UUID> uuidList);
}
