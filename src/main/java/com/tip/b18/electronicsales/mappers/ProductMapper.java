package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.ProductDTO;
import com.tip.b18.electronicsales.entities.Brand;
import com.tip.b18.electronicsales.entities.Category;
import com.tip.b18.electronicsales.entities.Product;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    List<ProductDTO> toProductDTOList(Page<ProductDTO> productDTOS);
    default Product toProduct(ProductDTO productDTO, Category category, Brand brand){
        Product product = new Product();
        product.setSku(productDTO.getSku());
        product.setName(productDTO.getName());
        product.setStock(productDTO.getStock());
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount());
        product.setDiscountPrice(productDTO.getDiscountPrice());
        product.setMainImageUrl(productDTO.getMainImageUrl());
        product.setDescription(productDTO.getDescription());
        product.setWarranty(productDTO.getWarranty());
        return product;
    }
}
