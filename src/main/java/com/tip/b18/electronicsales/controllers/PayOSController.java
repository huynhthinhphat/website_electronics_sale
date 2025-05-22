package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.dto.OrderDTO;
import com.tip.b18.electronicsales.services.PayOSService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class PayOSController {
    private final PayOSService payOSService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> createPayment(@RequestBody OrderDTO orderDTO){
        return ResponseEntity.ok(payOSService.createPayment(orderDTO));
    }
}
