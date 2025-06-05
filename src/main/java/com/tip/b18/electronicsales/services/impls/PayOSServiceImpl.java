package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.OrderDTO;
import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.exceptions.PayOSException;
import com.tip.b18.electronicsales.services.PayOSService;
import com.tip.b18.electronicsales.utils.SignatureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayOSServiceImpl implements PayOSService {
    @Value("${PAYOS_CLIENT_ID}")
    private String clientId;

    @Value("${PAYOS_API_KEY}")
    private String apiKey;

    @Value("${PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    @Value("${key.secret}")
    private String secretKey;

    @Override
    public ResponseEntity<?> createPayment(OrderDTO orderDTO){
        if(orderDTO != null){
            try{
                String orderCode = orderDTO.getOrderCode();
                long amount = orderDTO.getTotalPrice().longValue();
                String cancelUrl = "http://localhost:4200/checkout/cancel?signature=" + SignatureUtil.generateHmacSHA256(orderCode, secretKey);
                String returnUrl = "http://localhost:4200/checkout/success?paymentMethod=MOMO";
                String description = " " + orderCode;
                String data = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("x-client-id", clientId);
                headers.set("x-api-key", apiKey);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = getMapHttpEntity(Long.parseLong(orderCode), amount, description, orderDTO.getItems(), data, returnUrl, cancelUrl, headers);

                // Gọi API PayOS tạo thanh toán
                // Lấy URL thanh toán từ response
                return restTemplate.postForEntity("https://api-merchant.payos.vn/v2/payment-requests", entity, Map.class);

            }catch (Exception e){
                throw new PayOSException(MessageConstant.ERROR_PAYOS);
            }
        }
        return null;
    }

    private HttpEntity<Map<String, Object>> getMapHttpEntity(long orderCode, long amount, String description, List<OrderDetailDTO> items, String data, String returnUrl, String cancelUrl, HttpHeaders headers) throws NoSuchAlgorithmException, InvalidKeyException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderCode", orderCode);
        payload.put("amount", amount);
        payload.put("description", description);
        payload.put("items", items);
        payload.put("signature", SignatureUtil.generateHmacSHA256(data, checksumKey));
        payload.put("returnUrl", returnUrl);
        payload.put("cancelUrl", cancelUrl);
        payload.put("expiredAt", LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        payload.put("webhookUrl", "https://2f70-2405-4802-6071-8720-1d40-6272-b9e0-aeeb.ngrok-free.app/api/webhook/payment-success");

        return new HttpEntity<>(payload, headers);
    }
}
