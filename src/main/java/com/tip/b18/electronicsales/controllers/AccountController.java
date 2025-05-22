package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.services.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<CustomPage<AccountDTO>> viewAccounts(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                                                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(name = "limit", required = false, defaultValue = "5") int limit){
        CustomPage<AccountDTO> accounts = accountService.viewAccounts(search, page, limit);

        ResponseDTO<CustomPage<AccountDTO>> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setData(accounts);

        return responseDTO;
    }

    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseDTO<AccountDTO> viewPersonalAccount(@RequestParam(name = "id", required = false) @Valid UUID id){
        AccountDTO accountDTO = accountService.viewPersonalAccount(id);

        ResponseDTO<AccountDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setData(accountDTO);

        return responseDTO;
    }

    @PutMapping("/personal")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseDTO<AccountDTO> updatePersonalAccount(@RequestBody @Valid AccountDTO accountDTO){
        AccountDTO account = accountService.updatePersonalAccount(accountDTO);

        ResponseDTO<AccountDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_UPDATE);
        responseDTO.setData(account);

        return responseDTO;
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseDTO<?> deleteAccount(@RequestParam(name = "id") @Valid UUID id){
        accountService.deleteAccount(id);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_DELETE);

        return responseDTO;
    }

    @PutMapping("update-password")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseDTO<?> changePasswordAccount(@RequestBody @Valid UpdatePasswordDTO updatePasswordDTO){
        accountService.changePassword(updatePasswordDTO);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();
        responseDTO.setStatus("success");
        responseDTO.setMessage(MessageConstant.SUCCESS_CHANGE);

        return responseDTO;
    }
}
