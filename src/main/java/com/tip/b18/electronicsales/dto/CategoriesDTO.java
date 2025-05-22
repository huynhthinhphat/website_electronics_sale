package com.tip.b18.electronicsales.dto;

import com.tip.b18.electronicsales.dto.base.BaseCategoryDTO;
import lombok.Data;
import java.util.UUID;

@Data
public class CategoriesDTO extends BaseCategoryDTO {
    private UUID id;
}
