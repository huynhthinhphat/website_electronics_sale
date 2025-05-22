package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.OrderDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter subscribe(String orderCode);
    void sendOrderPaidEvent(String orderCode);
}
