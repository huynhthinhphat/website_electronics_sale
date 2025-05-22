package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.services.AccountService;
import com.tip.b18.electronicsales.services.GoogleAuthService;
import com.tip.b18.electronicsales.services.JwtService;
import com.tip.b18.electronicsales.services.OrderService;
import com.tip.b18.electronicsales.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final @Lazy AccountService accountService;
    private final JwtService jwtService;
    private final GoogleAuthService googleAuthService;
    private final OrderService orderService;

    @PostMapping("/login")
    public ResponseDTO<AccountDTO> loginAccount(@RequestBody @Valid AccountLoginDTO accountLoginDTO, HttpServletResponse response){
        AccountDTO account = accountService.loginAccount(accountLoginDTO);

        CookieUtil.addJwtToCookie(response, jwtService.generateToken(account.getUserName(), account.getId(), account.isRole()));

        ResponseDTO<AccountDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_ACCOUNT_LOGGED_IN);
        responseDTO.setData(account);
        return responseDTO;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<AccountDTO>> registerAccount(@RequestBody @Valid AccountRegisterDTO accountRegisterDTO){
        accountService.registerAccount(accountRegisterDTO);

        ResponseDTO<AccountDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_ACCOUNT_REGISTERED);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login-google")
    public ResponseDTO<?> loginGoogle(@RequestHeader("Authorization") String accessToken, HttpServletResponse response) {
        AccountDTO account = googleAuthService.authenticateWithGoogle(accessToken.replace("Bearer ", ""));

        CookieUtil.addJwtToCookie(response, jwtService.generateToken(account.getUserName(), account.getId(), account.isRole()));

        ResponseDTO<AccountDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_ACCOUNT_LOGGED_IN);
        responseDTO.setData(account);

        return responseDTO;
    }

    @PutMapping("update-password")
    public ResponseDTO<?> changePasswordAccount(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        accountService.changePasswordAfterVerifyOTP(updatePasswordDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_CHANGE);

        return responseDTO;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(HttpServletResponse response){
        CookieUtil.deleteJwtCookie(response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public void test(){
        orderService.scheduleOrderStatusCheck();
    }
}
