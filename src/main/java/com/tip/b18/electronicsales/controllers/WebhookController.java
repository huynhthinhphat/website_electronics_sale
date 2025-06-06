package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final OrderService orderService;

    @PostMapping("/payment-success")
    public void handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
        orderService.updateStatusOrder(payload);
    }
}
