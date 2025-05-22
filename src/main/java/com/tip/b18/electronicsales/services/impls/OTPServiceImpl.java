package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.services.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String generateOtp(String userName) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(userName, otpCode, Duration.ofMinutes(5));
        return otpCode;
    }

    @Override
    public boolean verifyOtp(String userName, String inputCode) {
        String savedOtp = redisTemplate.opsForValue().get(userName);
        return inputCode.equals(savedOtp);
    }

    @Override
    public void deleteOtpInRedis(String email) {
        redisTemplate.delete(email);
    }

    @Override
    public void clearOtp(String email) {
        redisTemplate.delete(email);
    }
}
