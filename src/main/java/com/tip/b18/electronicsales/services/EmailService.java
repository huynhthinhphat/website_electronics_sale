package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.Order;

import java.util.List;

public interface EmailService {
    void sendOTP(String userName);
    void verifyOTP(String userName, String OTP);
    void sendBill(String fullName, String email, Order order, List<OrderDetailDTO> orderDetailDTOList);
}
