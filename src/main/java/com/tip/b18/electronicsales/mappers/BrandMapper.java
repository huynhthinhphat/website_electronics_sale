package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.BrandsDTO;
import com.tip.b18.electronicsales.entities.Brand;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    List<BrandsDTO> toBrandsDTO(Page<Brand> brands);
}
