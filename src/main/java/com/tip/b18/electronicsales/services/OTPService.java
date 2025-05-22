package com.tip.b18.electronicsales.services;

public interface OTPService {
    String generateOtp(String userName);
    boolean verifyOtp(String userName, String inputCode);
    void deleteOtpInRedis(String userName);
    void clearOtp(String userName);
}
