package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.CategoriesDTO;
import com.tip.b18.electronicsales.entities.Category;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    List<CategoriesDTO> toCategoriesDTO(Page<Category> categories);
}
