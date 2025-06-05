package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.ResponseDTO;
import com.tip.b18.electronicsales.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping
    public ResponseDTO<String> sentMail(@RequestParam String userName){
        emailService.sendMail(userName);
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_SEND_EMAIL);
        return responseDTO;
    }

    @PostMapping("/verify")
    public ResponseDTO<String> verifyOTP(@RequestParam String userName, @RequestParam String OTP){
        emailService.verifyOTP(userName, OTP);
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.VALID_OTP);
        return responseDTO;
    }
}
