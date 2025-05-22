package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.CategoriesDTO;
import com.tip.b18.electronicsales.dto.CategoryDTO;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.PageInfoDTO;
import com.tip.b18.electronicsales.entities.Category;
import com.tip.b18.electronicsales.exceptions.AlreadyExistsException;
import com.tip.b18.electronicsales.exceptions.IntegrityConstraintViolationException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.CategoryMapper;
import com.tip.b18.electronicsales.repositories.CategoryRepository;
import com.tip.b18.electronicsales.services.CategoryService;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CustomPage<CategoriesDTO> viewCategories(String search, int page, int limit) {
        CustomPage<CategoriesDTO> categoriesDTO = new CustomPage<>();;
        Pageable pageable = Pageable.unpaged();
        Page<Category> categories;

        if(SecurityUtil.isAdminRole()){
            if(limit > 0){
                pageable = PageRequest.of(page, limit);
            }
            categories = categoryRepository.findByCategoryName(search, pageable);

            PageInfoDTO pageInfoDTO = new PageInfoDTO();
            pageInfoDTO.setTotalPages(categories.getTotalPages());
            pageInfoDTO.setTotalElements(categories.getTotalElements());

            categoriesDTO.setPageInfo(pageInfoDTO);
        }else{
            categories = categoryRepository.findAll(pageable);

            categories.forEach(category -> {category.setDescription(null);});
        }
        categoriesDTO.setItems(categoryMapper.toCategoriesDTO(categories));

        return categoriesDTO;
    }

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        if(categoryRepository.existsByName(categoryDTO.getName())){
            throw new AlreadyExistsException(MessageConstant.ERROR_CATEGORY_NAME_EXISTS);
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        try{
            Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_CATEGORY));
            categoryRepository.delete(category);
        }catch (DataIntegrityViolationException e){
            throw new IntegrityConstraintViolationException(MessageConstant.ERROR_CATEGORY_HAS_PRODUCTS);
        }
    }

    @Override
    public void updateCategory(UUID id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_CATEGORY));

        String name = categoryDTO.getName();
        String description = categoryDTO.getDescription();

        boolean isChange = false;

        if(name != null && !name.equals(category.getName())){
            if(categoryRepository.existsByName(name)){
                throw new AlreadyExistsException(MessageConstant.ERROR_CATEGORY_NAME_EXISTS);
            }
            category.setName(name);
            isChange = true;
        }
        if(description != null && !description.equals(category.getDescription())){
            category.setDescription(description);
            isChange = true;
        }

        if(isChange){
            categoryRepository.save(category);
        }
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_CATEGORY));
    }
}
