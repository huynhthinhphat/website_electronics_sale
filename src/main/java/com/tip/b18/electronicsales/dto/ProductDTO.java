package com.tip.b18.electronicsales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private UUID id;
    private String sku;
    private String name;
    private Integer stock;
    private String category;
    private String brand;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal discountPrice;
    private Integer quantitySold;
    private Double rate;
    private String mainImageUrl;
    private Integer warranty;
    private String description;
    private List<String> colors;
    private List<String> images;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double star;
}
