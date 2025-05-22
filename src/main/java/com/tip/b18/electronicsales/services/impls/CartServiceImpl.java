package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.CartDTO;
import com.tip.b18.electronicsales.dto.CartItemDTO;
import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.entities.*;
import com.tip.b18.electronicsales.exceptions.InsufficientStockException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.repositories.CartRepository;
import com.tip.b18.electronicsales.services.*;
import com.tip.b18.electronicsales.utils.CompareUtil;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final ProductService productService;
    private final @Lazy AccountService accountService;
    private final ProductColorService productColorService;
    private final ColorService colorService;

    @Override
    public List<CartItemDTO> viewItemsInCart() {
        Cart cart = cartRepository.findByAccountId(SecurityUtil.getAuthenticatedUserId());
        if(cart == null) {
            return List.of();
        }
        return cartItemService.findAllByCartId(cart.getId());
    }

    @Override
    @Transactional
    public void addItemToCart(CartItemDTO cartItemDTO) {
        Product product = checkExitsColorAndProductAndProductColor(cartItemDTO);

        if(CompareUtil.isFirstNumberSmaller(product.getStock(), cartItemDTO.getQuantity())){
            throw new InsufficientStockException(String.format(MessageConstant.ERROR_INSUFFICIENT_STOCK, product.getName()));
        }

        Cart cart = cartRepository.findByAccountId(SecurityUtil.getAuthenticatedUserId());
        boolean isNewCart = (cart == null);

        if(isNewCart) {
            Account account = accountService.findById(SecurityUtil.getAuthenticatedUserId());

            cart = new Cart();
            cart.setAccount(account);
            cart.setTotalQuantity(0);
            cart.setTotalPrice(BigDecimal.ZERO);
        }
        cart.setTotalPrice(cart.getTotalPrice().add(product.getDiscountPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()))));

        if(!cartItemService.existCartItem(cart, product, cartItemDTO.getColor())){
            cart.setTotalQuantity(cart.getTotalQuantity() + 1);
        }

        cartItemService.addNewItemToCart(cartRepository.save(cart), product, cartItemDTO);
    }

    @Override
    @Transactional
    public void deleteItemsInCart(CustomList<UUID> cartItemIdList) {
        Cart cart = cartRepository.findByAccountId(SecurityUtil.getAuthenticatedUserId());
        if (cart == null){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_CART);
        }

        List<CartItem> cartItems = cartItemService.deleteItemsInCart(cartItemIdList);
        if(cartItems.isEmpty()){
            return;
        }

        int totalItems = cartItems.size();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems){
            totalPrice = totalPrice.add(cartItem.getTotalPrice());
        }
        cart.setTotalQuantity(cart.getTotalQuantity() - totalItems);
        cart.setTotalPrice(cart.getTotalPrice().subtract(totalPrice));

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void updateItemsInCart(List<CartItemDTO> cartItemDTO) {
        Cart cart = cartRepository.findByAccountId(SecurityUtil.getAuthenticatedUserId());
        if(cart == null){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_CART);
        }

        cartItemService.updateQuantityOrColorItemsInCart(cart, cartItemDTO);
        updateTotalPriceAndTotalQuantityOfCart(cart);
    }

    @Override
    public Product checkExitsColorAndProductAndProductColor(CartItemDTO cartItemDTO) {
        Color color = colorService.findByColorName(cartItemDTO.getColor());
        Product product = productService.findProductById(cartItemDTO.getId());
        productColorService.findByProductIdAndColorId(product, color, cartItemDTO.getColor());

        return product;
    }

    @Override
    public int getTotalQuantityItemInCartByAccountId() {
        Cart cart = cartRepository.findByAccountId(SecurityUtil.getAuthenticatedUserId());
        return cart == null ? 0 : cart.getTotalQuantity();
    }

    @Override
    public int getTotalQuantityItemInCartByAccountId(UUID accountId) {
        Cart cart = cartRepository.findByAccountId(accountId);
        return cart == null ? 0 : cart.getTotalQuantity();
    }

    @Override
    public void updateTotalPriceAndTotalQuantityOfCart(Cart cart) {
        Tuple tuple = cartItemService.calculatorTotalPriceAndTotalQuantityOfCart(cart.getId());

        BigDecimal totalPrice = tuple.get("totalPrice", BigDecimal.class);
        Long totalQuantity = tuple.get("totalQuantity", Long.class);

        cart.setTotalPrice(totalPrice == null ? BigDecimal.ZERO : totalPrice);
        cart.setTotalQuantity(totalQuantity.intValue());

        cartRepository.save(cart);
    }

    @Override
    public CartDTO getTotalQuantityItem() {
        int totalQuantity = getTotalQuantityItemInCartByAccountId();
        return new CartDTO(totalQuantity);
    }

    @Override
    public Cart findByAccountId(UUID accountId) {
        return cartRepository.findByAccountId(accountId);
    }
}