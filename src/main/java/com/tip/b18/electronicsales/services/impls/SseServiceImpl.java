package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.services.SseService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseServiceImpl implements SseService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(String orderCode) {
        // Sử dụng timeout dài hơn (30 phút = 1800000ms)
        SseEmitter emitter = new SseEmitter(1800000L);
        emitters.put(orderCode, emitter);

        // Gửi một sự kiện ban đầu để thiết lập kết nối
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Kết nối đã được thiết lập cho đơn hàng: " + orderCode));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        emitter.onCompletion(() -> {
            emitters.remove(orderCode);
        });

        emitter.onTimeout(() -> {
            emitters.remove(orderCode);
        });

        emitter.onError((e) -> {
            emitters.remove(orderCode);
        });

        return emitter;
    }

    @Override
    public void sendOrderPaidEvent(String orderCode) {
        SseEmitter emitter = emitters.get(orderCode);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("order-paid") // Tên sự kiện phải khớp với addEventListener bên client
                        .data("Successfully order"));

                // Chờ một chút để đảm bảo sự kiện được gửi đi
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            } finally {
                emitters.remove(orderCode);
            }
        } else {
            System.out.println("Không tìm thấy emitter cho mã đơn hàng: " + orderCode);
        }
    }
}
