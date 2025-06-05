package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.CategoryDTO;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.entities.Category;

import java.util.UUID;

public interface CategoryService {
    CustomPage<CategoryDTO> viewCategories(String search, int page, int limit);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    void deleteCategory(UUID id);
    void updateCategory(UUID id, CategoryDTO categoryDTO);
    Category getCategoryById(UUID id);
}
