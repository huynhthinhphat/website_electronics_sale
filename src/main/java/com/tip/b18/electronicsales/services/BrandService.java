package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.BrandDTO;
import com.tip.b18.electronicsales.dto.BrandsDTO;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.entities.Brand;

import java.util.UUID;

public interface BrandService {
    void addBrand(BrandDTO brandDTO);
    CustomPage<BrandsDTO> viewBrands(String search, int page, int limit);
    void deleteBrand(UUID id);
    void updateBrand(UUID id, BrandDTO brandDTO);
    Brand getBrandById(UUID id);
}
