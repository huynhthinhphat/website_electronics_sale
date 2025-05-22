package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.OrderDTO;
import org.springframework.http.ResponseEntity;

public interface PayOSService {
    ResponseEntity<?> createPayment(OrderDTO orderDTO);
}
