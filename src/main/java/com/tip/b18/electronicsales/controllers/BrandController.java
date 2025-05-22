package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.services.BrandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class BrandController {
    private final BrandService brandService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<BrandDTO>> addBrand(@RequestBody BrandDTO brandDTO){
        brandService.addBrand(brandDTO);

        ResponseDTO<BrandDTO> responseDTO = new ResponseDTO<>();

        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_ADD);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseDTO<CustomPage<BrandsDTO>> viewBrands(@RequestParam(name = "search", defaultValue = "") String search,
                                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "limit", defaultValue = "-1") int limit){
        ResponseDTO<CustomPage<BrandsDTO>> responseDTO = new ResponseDTO<>();

        responseDTO.setStatus("success");
        responseDTO.setData(brandService.viewBrands(search, page, limit));

        return responseDTO;
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<?> deleteBrand(@RequestParam @Valid UUID id){
        brandService.deleteBrand(id);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_DELETE);

        return responseDTO;
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<?> updateBrand(@RequestParam @Valid UUID id, @RequestBody BrandDTO brandDTO){
        brandService.updateBrand(id, brandDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_UPDATE);

        return responseDTO;
    }
}
