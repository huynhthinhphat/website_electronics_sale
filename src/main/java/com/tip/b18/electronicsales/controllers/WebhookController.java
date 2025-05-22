package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.services.OrderService;
import com.tip.b18.electronicsales.services.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final OrderService orderService;
    private final SseService sseService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam String orderCode) {
        return sseService.subscribe(orderCode);
    }

    @PostMapping("/payment-success")
    public void handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
        orderService.updateStatusOrder(payload);
    }
}
