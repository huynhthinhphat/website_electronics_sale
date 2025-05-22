package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.CartDTO;
import com.tip.b18.electronicsales.dto.CartItemDTO;
import com.tip.b18.electronicsales.dto.CustomList;
import com.tip.b18.electronicsales.dto.ResponseDTO;
import com.tip.b18.electronicsales.services.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class CartController {
    private final @Lazy CartService cartService;

    @GetMapping
    public ResponseDTO<List<CartItemDTO>> viewItemsInCart(){
        ResponseDTO<List<CartItemDTO>> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setData(cartService.viewItemsInCart());
        return responseDTO;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<CartDTO>> addItemToCart(@RequestBody @Valid CartItemDTO cartItemDTO){
        cartService.addItemToCart(cartItemDTO);

        ResponseDTO<CartDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_ADD);
        responseDTO.setData(cartService.getTotalQuantityItem());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @DeleteMapping
    public ResponseDTO<CartDTO> deleteItemsInCart(@RequestBody @Valid CustomList<UUID> cartItemIdList){
        cartService.deleteItemsInCart(cartItemIdList);

        ResponseDTO<CartDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_DELETE);
        responseDTO.setData(cartService.getTotalQuantityItem());

        return responseDTO;
    }

//    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PATCH})
    @PatchMapping
    public ResponseDTO<?> updateItemsInCart(@RequestBody @Valid List<CartItemDTO> cartItemDTO){
        cartService.updateItemsInCart(cartItemDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_UPDATE);

        return responseDTO;
    }
}
