package com.tip.b18.electronicsales.dto;

import com.tip.b18.electronicsales.dto.base.BaseBrandDTO;
import lombok.Data;
import java.util.UUID;

@Data
public class BrandsDTO extends BaseBrandDTO {
    private UUID id;
}
