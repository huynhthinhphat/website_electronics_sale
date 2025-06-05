package com.tip.b18.electronicsales.mappers;

import com.tip.b18.electronicsales.dto.CategoryDTO;
import com.tip.b18.electronicsales.entities.Category;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    List<CategoryDTO> toCategoriesDTO(Page<Category> categories);
    default CategoryDTO toCategoryDTO(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
