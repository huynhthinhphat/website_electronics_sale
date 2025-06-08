package com.tip.b18.electronicsales.services;

import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.entities.Account;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AccountService {
    AccountDTO loginAccount(AccountLoginDTO accountLoginDTO);
    void registerAccount(AccountRegisterDTO accountRegisterDTO);
    CustomPage<AccountDTO> viewAccounts(String search, int page, int limit);
    AccountDTO viewPersonalAccount(UUID id);
    AccountDTO updatePersonalAccount(AccountDTO accountDTO);
    void deleteAccount(UUID id);
    void changePassword(UpdatePasswordDTO updatePasswordDTO);
    void changePasswordAfterVerifyOTP(UpdatePasswordDTO updatePasswordDTO);
    Account findById(UUID id);
    int getQuantityNewCustomers(LocalDateTime startDay, LocalDateTime endDay);
    Account findByEmail(String email);
    Account findByUserName(String userName);
    AccountDTO getRoleAndTotalQuantityItemInCart();
}
