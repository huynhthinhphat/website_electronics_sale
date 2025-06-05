package com.tip.b18.electronicsales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {
    private UUID id;
    private UUID orderDetailId;
    private String fullName;
    private String mainImage;
    private String avatar;
    private String productName;
    private Double productStar;
    private String color;
    private String description;
    private int star;
    private List<String> images;
    private LocalDateTime createdAt;
}
