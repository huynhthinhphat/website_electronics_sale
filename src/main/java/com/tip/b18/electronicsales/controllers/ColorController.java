package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.services.ColorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ColorController {
    private final ColorService colorService;

    @GetMapping
    public ResponseDTO<List<String>> viewColors(){
        ResponseDTO<List<String>> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setData(colorService.findAll());
        return responseDTO;
    }
}
