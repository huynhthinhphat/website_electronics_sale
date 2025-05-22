package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.CartItemDTO;
import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.CartItem;
import com.tip.b18.electronicsales.entities.Product;
import com.tip.b18.electronicsales.entities.Cart;
import com.tip.b18.electronicsales.exceptions.InsufficientStockException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.CartItemMapper;
import com.tip.b18.electronicsales.repositories.CartItemRepository;
import com.tip.b18.electronicsales.services.CartItemService;
import com.tip.b18.electronicsales.services.ProductColorService;
import com.tip.b18.electronicsales.utils.CompareUtil;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ProductColorService productColorService;

    @Override
    public List<CartItemDTO> findAllByCartId(UUID uuid) {
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(uuid);

        Set<UUID> productIds = cartItems
                .stream()
                .map(cartItem -> cartItem.getProduct().getId())
                .collect(Collectors.toSet());

        Map<UUID, List<String>> cartItemColors = productColorService.getColorsByProductIds(List.copyOf(productIds));

        return cartItemMapper.toCartItemList(cartItemRepository.findAllByCartId(uuid), cartItemColors);
    }

    @Override
    public void addNewItemToCart(Cart cart, Product product, CartItemDTO cartItemDTO) {
        CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndColor(cart.getId(), product.getId(), cartItemDTO.getColor());
        boolean newCartItem = (cartItem == null);

        if(newCartItem){
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setColor(cartItemDTO.getColor());
            cartItem.setTotalPrice(BigDecimal.ZERO);
            cartItem.setQuantity(0);
        }

        if(CompareUtil.isFirstNumberSmaller(product.getStock(), cartItem.getQuantity() + cartItemDTO.getQuantity())){
            throw new InsufficientStockException(String.format(MessageConstant.ERROR_INSUFFICIENT_STOCK, product.getName()));
        }

        cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
        cartItem.setTotalPrice(cartItem.getTotalPrice().add(product.getDiscountPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()))));

        cartItemRepository.save(cartItem);
    }

    @Override
    public boolean existCartItem(Cart cart, Product product, String color) {
        return cartItemRepository.findByCartIdAndProductIdAndColor(cart.getId(), product.getId(), color) != null;
    }

    @Override
    public List<CartItem> deleteItemsInCart(CustomList<UUID> uuidList) {
        List<CartItem> cartItems = cartItemRepository.findAllByIdIn(uuidList.getItems());
        cartItemRepository.deleteAll(cartItems);

        return cartItems;
    }

    @Override
    public void updateQuantityOrColorItemsInCart(Cart cart, List<CartItemDTO> cartItemDTOs) {
        List<UUID> productIds = cartItemDTOs
                .stream()
                .map(CartItemDTO::getId)
                .toList();
        List<String> colors = cartItemDTOs
                .stream()
                .map(CartItemDTO::getColor)
                .toList();

        List<CartItem> cartItems = cartItemRepository.findAllByCartIdAndProductIdInAndColorIn(cart.getId(), productIds, colors);
        if(cartItems.isEmpty()){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_PRODUCTS_IN_CART);
        }

        Map<String, Integer> cartItemDTOMap = cartItemDTOs
                .stream()
                .collect(Collectors.toMap(dto -> dto.getId() + dto.getColor(), CartItemDTO::getQuantity));
        List<CartItem> cartItemListToUpdate = cartItems
                .stream()
                .filter(cartItem -> cartItemDTOMap.containsKey(cartItem.getProduct().getId() + cartItem.getColor()))
                .peek(cartItem -> {
                    int quantity = cartItemDTOMap.get(cartItem.getProduct().getId() + cartItem.getColor());
                    if(quantity > 0){
                        if(CompareUtil.isFirstNumberSmaller(cartItem.getProduct().getStock(),quantity)){
                            throw new InsufficientStockException(String.format(MessageConstant.ERROR_INSUFFICIENT_STOCK, cartItem.getProduct().getName()));
                        }
                    }
                    cartItem.setQuantity(quantity);
                    cartItem.setTotalPrice(cartItem.getProduct().getDiscountPrice().multiply(BigDecimal.valueOf(quantity)));
                })
                .toList();
        cartItemRepository.saveAll(cartItemListToUpdate);
    }

    @Override
    public Tuple calculatorTotalPriceAndTotalQuantityOfCart(UUID cartId) {
        return cartItemRepository.calculatorTotalPriceAndTotalQuantityOfCart(cartId);
    }

    @Override
    public List<UUID> getCartItemsToDelete(Cart cart, List<OrderDetailDTO> orderDetailDTOList) {
        List<UUID> productIds = orderDetailDTOList
                .stream()
                .map(OrderDetailDTO::getId)
                .toList();
        List<String> colors = orderDetailDTOList
                .stream()
                .map(OrderDetailDTO::getColor)
                .toList();
        List<CartItem> cartItems = cartItemRepository.findAllByCartIdAndProductIdInAndColorIn(
                cart.getId(),
                productIds,
                colors);
        return cartItems
                .stream()
                .map(CartItem::getId)
                .toList();
    }

    @Override
    public void deleteItemsInCart(List<UUID> uuidList) {
        cartItemRepository.deleteAll(cartItemRepository.findAllByIdIn(uuidList));
    }
}
