package com.tip.b18.electronicsales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

@Data
public class BrandDTO {
    private UUID id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
}
