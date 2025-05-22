package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.dto.ResponseDTO;
import com.tip.b18.electronicsales.services.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseDTO<CustomPage<ProductDTO>> viewProducts(@RequestParam(name = "search", defaultValue = "") String search,
                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                            @RequestParam(name = "limit", defaultValue = "6") int limit,
                                                            @RequestParam(name = "categoryId", defaultValue = "") UUID categoryId,
                                                            @RequestParam(name = "brandId", defaultValue = "") UUID brandId,
                                                            @Parameter(description = "Chọn kiểu sắp xếp", schema = @Schema(allowableValues = {"newest", "bestseller", "priceAsc", "priceDesc", "priceDiscountAsc", "priceDiscountDesc"}))
                                                            @RequestParam(name = "orderBy", defaultValue = "") String orderBy){
        ResponseDTO<CustomPage<ProductDTO>> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setData(productService.viewProducts(search, page, limit, categoryId, brandId, orderBy));

        return responseDTO;
    }

    @GetMapping("/details")
    public ResponseDTO<ProductDTO> viewProductDetails(@RequestParam(name = "id") @Valid UUID id){
        ResponseDTO<ProductDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setData(productService.viewProductDetails(id));

        return responseDTO;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<?>> addProduct(@RequestBody @Valid ProductDTO productDTO){
        productService.addProduct(productDTO);

        ResponseDTO<ProductDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_ADD);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<?> deleteProduct(@RequestParam @Valid UUID id){
        productService.deleteProduct(id);

        ResponseDTO<ProductDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_DELETE);

        return responseDTO;
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<?> updateProduct(@RequestParam("id") @Valid UUID id, @RequestBody @Valid ProductDTO productDTO){
        productService.updateProduct(id, productDTO);

        ResponseDTO<ProductDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_UPDATE);

        return responseDTO;
    }
}
