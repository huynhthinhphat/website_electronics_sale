package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.BrandDTO;
import com.tip.b18.electronicsales.dto.BrandsDTO;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.PageInfoDTO;
import com.tip.b18.electronicsales.entities.Brand;
import com.tip.b18.electronicsales.exceptions.AlreadyExistsException;
import com.tip.b18.electronicsales.exceptions.IntegrityConstraintViolationException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.BrandMapper;
import com.tip.b18.electronicsales.repositories.BrandRepository;
import com.tip.b18.electronicsales.services.BrandService;
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
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public void addBrand(BrandDTO brandDTO) {
        if(brandRepository.existsByName(brandDTO.getName())){
            throw new AlreadyExistsException(MessageConstant.ERROR_BRAND_NAME_EXISTS);
        }
        Brand brand = new Brand();

        brand.setName(brandDTO.getName());
        brand.setDescription(brandDTO.getDescription());

        brandRepository.save(brand);
    }

    @Override
    public CustomPage<BrandsDTO> viewBrands(String search, int page, int limit) {
        CustomPage<BrandsDTO> brandsDTO = new CustomPage<>();
        Pageable pageable = Pageable.unpaged();;
        Page<Brand> brands;

        if(SecurityUtil.isAdminRole()){
            if(limit > 0){
                pageable = PageRequest.of(page, limit);
            }
            brands = brandRepository.findByBrandName(search, pageable);

            PageInfoDTO pageInfoDTO = new PageInfoDTO();
            pageInfoDTO.setTotalPages(brands.getTotalPages());
            pageInfoDTO.setTotalElements(brands.getTotalElements());

            brandsDTO.setPageInfo(pageInfoDTO);
        }else{
            brands = brandRepository.findAll(pageable);

            brands.forEach(brand -> {brand.setDescription(null);});
        }
        brandsDTO.setItems(brandMapper.toBrandsDTO(brands));

        return brandsDTO;
    }

    @Override
    public void deleteBrand(UUID id) {
        try{
            Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_BRAND));
            brandRepository.delete(brand);
        }catch (DataIntegrityViolationException e){
            throw new IntegrityConstraintViolationException(MessageConstant.ERROR_BRAND_HAS_PRODUCTS);
        }
    }

    @Override
    public void updateBrand(UUID id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_BRAND));

        String name = brandDTO.getName();
        String description = brandDTO.getDescription();

        boolean isChange = false;

        if(name != null && !name.equals(brand.getName())){
            if(brandRepository.existsByName(name)){
                throw new AlreadyExistsException(MessageConstant.ERROR_BRAND_NAME_EXISTS);
            }
            brand.setName(name);
            isChange = true;
        }
        if(description != null && !description.equals(brand.getDescription())){
            brand.setDescription(description);
            isChange = true;
        }

        if(isChange){
            brandRepository.save(brand);
        }
    }

    @Override
    public Brand getBrandById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_BRAND));
    }
}
