package com.tip.b18.electronicsales.services;

public interface EmailService {
    void sendMail(String userName);
    void verifyOTP(String userName, String OTP);
}
