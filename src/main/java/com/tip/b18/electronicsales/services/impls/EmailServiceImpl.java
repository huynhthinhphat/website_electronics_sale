package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.exceptions.CredentialsException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.services.AccountService;
import com.tip.b18.electronicsales.services.EmailService;
import com.tip.b18.electronicsales.services.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final OTPService otpService;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final AccountService accountService;

    @Override
    public void sendMail(String userName) {
        String OTP = otpService.generateOtp(userName);
        Account account = accountService.findByUserName(userName);
        if(account.getEmail() == null){
            throw new NotFoundException(MessageConstant.ERROR_NO_GMAIL_LINKED);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(account.getEmail());
        message.setSubject(MessageConstant.TITLE_FORGOT_PASSWORD);
        message.setText("Mã OTP của bạn là: " + OTP);

        javaMailSender.send(message);
    }

    @Override
    public void verifyOTP(String userName, String OTP) {
        if(!otpService.verifyOtp(userName, OTP)){
            throw new CredentialsException(MessageConstant.ERROR_INVALID_OTP);
        }
        otpService.deleteOtpInRedis(userName);
    }
}
