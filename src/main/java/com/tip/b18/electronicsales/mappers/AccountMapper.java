package com.tip.b18.electronicsales.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.tip.b18.electronicsales.dto.AccountDTO;
import com.tip.b18.electronicsales.dto.AccountRegisterDTO;
import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.utils.AccountUtil;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO toDTO(Account account);
    default AccountDTO toDTO(Account account, int totalQuantity){
        AccountDTO.AccountDTOBuilder accountDTO = AccountDTO.builder();
        accountDTO.id( account.getId() );
        accountDTO.fullName( account.getFullName() );
        accountDTO.userName( account.getUserName() );
        accountDTO.role( account.isRole() );
        accountDTO.gender( account.getGender() );
        accountDTO.phoneNumber( account.getPhoneNumber() );
        accountDTO.email( account.getEmail() );
        accountDTO.address( account.getAddress() );
        accountDTO.avatarUrl( account.getAvatarUrl() );
        accountDTO.dateOfBirth( account.getDateOfBirth() );
        accountDTO.totalQuantity(totalQuantity);
        return accountDTO.build();
    }

    default List<AccountDTO> toDTOList(Page<Account> accounts){
        return accounts
                .stream()
                .map(account -> AccountDTO
                        .builder()
                        .id(account.getId())
                        .fullName(account.getFullName())
                        .userName(account.getUserName())
                        .role(account.isRole())
                        .gender(account.getGender())
                        .phoneNumber(account.getPhoneNumber())
                        .build())
                .toList();
    }

    default AccountDTO toAccountPersonalDTO(Account account){
            return AccountDTO
                    .builder()
                    .fullName(account.getFullName())
                    .userName(account.getUserName())
                    .email(account.getEmail())
                    .dateOfBirth(account.getDateOfBirth())
                    .phoneNumber(account.getPhoneNumber())
                    .address(account.getAddress())
                    .role(account.isRole())
                    .address(account.getAddress())
                    .build();
    }

    default AccountDTO toAccountUpdateDto(Account account, int totalQuantity){
        return AccountDTO
                .builder()
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phoneNumber(account.getPhoneNumber())
                .dateOfBirth(account.getDateOfBirth())
                .address(account.getAddress())
                .gender(account.getGender())
                .avatarUrl(account.getAvatarUrl())
                .totalQuantity(totalQuantity)
                .build();
    }

    default Account toAccountEntity(AccountRegisterDTO accountRegisterDTO, String password){
        Account account = new Account();

        account.setFullName(accountRegisterDTO.getFullName());
        account.setUserName(accountRegisterDTO.getUserName());
        account.setPassword(password);
        account.setRole(false);
        account.setGender(accountRegisterDTO.getGender());

        if(accountRegisterDTO.getEmail() != null){
            account.setEmail(accountRegisterDTO.getEmail());
            account.setAvatarUrl(accountRegisterDTO.getAvatarUrl());
        }

        return account;
    }

    default AccountRegisterDTO toAccountRegister(JsonNode body, String email, Boolean gender){
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        accountRegisterDTO.setUserName(AccountUtil.generateUserName());
        accountRegisterDTO.setPassword(UUID.randomUUID().toString());
        accountRegisterDTO.setEmail(email);
        accountRegisterDTO.setFullName(body.path("names").path(0).path("displayName").asText(null));
        accountRegisterDTO.setAvatarUrl(body.path("photos").path(0).path("url").asText(null));
        accountRegisterDTO.setGender(gender);
        return accountRegisterDTO;
    }
}
