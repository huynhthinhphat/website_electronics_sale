package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.CategoriesDTO;
import com.tip.b18.electronicsales.dto.CategoryDTO;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.ResponseDTO;
import com.tip.b18.electronicsales.services.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseDTO<CustomPage<CategoriesDTO>> viewCategories(@RequestParam(name = "search", defaultValue = "") String search,
                                                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                                                 @RequestParam(name = "limit", defaultValue = "-1") int limit){
        ResponseDTO<CustomPage<CategoriesDTO>> responseDTO = new ResponseDTO<>();

        responseDTO.setStatus("success");
        responseDTO.setData(categoryService.viewCategories(search, page, limit));

        return responseDTO;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<CategoryDTO>> addCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.addCategory(categoryDTO);

        ResponseDTO<CategoryDTO> responseDTO = new ResponseDTO<>();

        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_ADD);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<?> deleteCategory(@RequestParam @Valid UUID id){
        categoryService.deleteCategory(id);

        ResponseDTO<CategoryDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_DELETE);

        return responseDTO;
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<?> updateCategory(@RequestParam @Valid UUID id, @RequestBody CategoryDTO categoryDTO){
        categoryService.updateCategory(id, categoryDTO);

        ResponseDTO<CategoryDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_UPDATE);

        return responseDTO;
    }
}