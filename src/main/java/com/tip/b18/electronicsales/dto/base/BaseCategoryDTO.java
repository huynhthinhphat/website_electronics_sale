package com.tip.b18.electronicsales.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class BaseCategoryDTO {
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
}
