package com.tip.b18.electronicsales.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CategoryDTO{
    private UUID id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
}
