package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.dto.*;
import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.exceptions.*;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.mappers.AccountMapper;
import com.tip.b18.electronicsales.repositories.AccountRepository;
import com.tip.b18.electronicsales.services.AccountService;
import com.tip.b18.electronicsales.services.CartService;
import com.tip.b18.electronicsales.services.CloudinaryService;
import com.tip.b18.electronicsales.services.PasswordService;
import com.tip.b18.electronicsales.utils.ImageUtil;
import com.tip.b18.electronicsales.utils.LocalDateTimeUtil;
import com.tip.b18.electronicsales.utils.SecurityUtil;
import com.tip.b18.electronicsales.utils.VietnameseCarrierUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordService passwordService;
    private final AccountMapper accountMapper;
    private final CloudinaryService cloudinaryService;
    private final @Lazy CartService cartService;

    @Override
    public AccountDTO loginAccount(AccountLoginDTO accountLoginDTO) {
        Account account = accountRepository.findByUserName(accountLoginDTO.getUserName())
                .orElseThrow(() -> new CredentialsException(MessageConstant.ERROR_INVALID_CREDENTIALS));

        if (!passwordService.matches(accountLoginDTO.getPassword(), account.getPassword())) {
            throw new CredentialsException(MessageConstant.ERROR_INVALID_CREDENTIALS);
        }

        AccountDTO accountDTO = accountMapper.toDTO(account);
        if(!account.isRole()){
            accountDTO.setTotalQuantity(cartService.getTotalQuantityItemInCartByAccountId(account.getId()));
        }

        return accountDTO;
    }

    @Override
    public void registerAccount(AccountRegisterDTO accountRegisterDTO) {
        accountRepository.findByUserName(accountRegisterDTO.getUserName())
                .ifPresent(account -> {
                    throw new AlreadyExistsException(MessageConstant.ERROR_ACCOUNT_EXISTS);
                });
        String password = passwordService.encryptPassword(accountRegisterDTO.getPassword());
        accountRepository.save(accountMapper.toAccountEntity(accountRegisterDTO, password));
    }

    @Override
    public CustomPage<AccountDTO> viewAccounts(String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Account> accounts = accountRepository.findByUserNameOrPhoneNumber(search, pageable);
        return new CustomPage<>(accountMapper.toDTOList(accounts), new PageInfoDTO(accounts.getTotalElements(), accounts.getTotalPages()));
    }

    @Override
    public AccountDTO viewPersonalAccount(UUID id) {
        return accountMapper.toAccountPersonalDTO(findById(SecurityUtil.getAuthenticatedUserId(id)));
    }

    @Override
    public AccountDTO updatePersonalAccount(AccountDTO accountDTO) {
        Account account = findById(SecurityUtil.getAuthenticatedUserId());

        String fullName = accountDTO.getFullName();
        String address = accountDTO.getAddress();
        String email = accountDTO.getEmail();
        String phoneNumber = accountDTO.getPhoneNumber();
        LocalDate dateOfBirth = accountDTO.getDateOfBirth();
        Boolean gender = accountDTO.getGender();
        String avatarUrl = accountDTO.getAvatarUrl();

        boolean isChange = false;

        if(fullName != null && !fullName.equals(account.getFullName())){
            account.setFullName(fullName);
            isChange = true;
        }
        if(!Objects.equals(address, account.getAddress())){
            account.setAddress(address);
            isChange = true;
        }
        if(!Objects.equals(email, account.getEmail())){
            if(email != null && accountRepository.existsByEmail(email)){
                throw new AlreadyExistsException(MessageConstant.ERROR_EMAIL_EXISTS);
            }
            account.setEmail(email);
            isChange = true;
        }
        if(!Objects.equals(phoneNumber, account.getPhoneNumber())){
            if(phoneNumber != null){
                VietnameseCarrierUtil.checkPhoneNumber(phoneNumber);
                if(accountRepository.existsByPhoneNumber(phoneNumber)){
                    throw new AlreadyExistsException(MessageConstant.ERROR_PHONE_NUMBER_EXISTS);
                }
            }
            account.setPhoneNumber(phoneNumber);
            isChange = true;
        }
        if(!Objects.equals(dateOfBirth, account.getDateOfBirth())){
            if(LocalDateTimeUtil.isAfter(dateOfBirth)){
                throw new InvalidValueException(MessageConstant.INVALID_DATE_AFTER_TODAY);
            }
            account.setDateOfBirth(dateOfBirth);
            isChange = true;
        }
        if(!Objects.equals(gender, account.getGender())){
            account.setGender(gender);
            isChange = true;
        }
        if(!Objects.equals(avatarUrl, account.getAvatarUrl())){
            try{
                cloudinaryService.deleteImage(ImageUtil.getPublicIdFromUrl(account.getAvatarUrl()));
            } catch (Exception e) {
                throw new CloudinaryDeleteException(MessageConstant.ERROR_CLOUDINARY);
            }
            account.setAvatarUrl(avatarUrl);
            isChange = true;
        }

        if(isChange){
            account = accountRepository.save(account);
        }
        return accountMapper.toAccountUpdateDto(account, cartService.getTotalQuantityItemInCartByAccountId());
    }

    @Override
    public void deleteAccount(UUID id) {
        try {
            Account account = findById(id);
            accountRepository.delete(account);
        }catch (DataIntegrityViolationException e){
            throw new IntegrityConstraintViolationException(MessageConstant.ERROR_ACCOUNT_HAS_ORDERS);
        }
    }

    @Override
    public void changePassword(UpdatePasswordDTO updatePasswordDTO) {
        UUID id = SecurityUtil.getAuthenticatedUserId();
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isEmpty()) {
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_ACCOUNT_TO_CHANGE_PASSWORD);
        }

        String oldPassword = updatePasswordDTO.getOldPassword();
        String newPassword = updatePasswordDTO.getNewPassword();

        if(oldPassword != null && newPassword != null){Account account = optionalAccount.get();
            String currentPassword = account.getPassword();

            if (!passwordService.matches(oldPassword, currentPassword)) {
                throw new InvalidPasswordException(MessageConstant.ERROR_INVALID_PASSWORD);
            } else if (passwordService.matches(newPassword, currentPassword)) {
                return;
            }
            account.setPassword(passwordService.encryptPassword(newPassword));
            accountRepository.save(account);
        }
    }

    @Override
    public void changePasswordAfterVerifyOTP(UpdatePasswordDTO updatePasswordDTO) {
        if(updatePasswordDTO.getUserName() == null){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_USERNAME_FROM_CLIENT);
        }

        Optional<Account> optionalAccount = accountRepository.findByUserName(updatePasswordDTO.getUserName());
        if(optionalAccount.isEmpty()){
            throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_ACCOUNT_TO_UPDATE_PASSWORD);
        }

        Account account = optionalAccount.get();

        String password = passwordService.encryptPassword(updatePasswordDTO.getNewPassword());
        account.setPassword(password);

        accountRepository.save(account);
    }

    @Override
    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_ACCOUNT));
    }

    @Override
    public int getQuantityNewCustomers(LocalDateTime startDay, LocalDateTime endDay) {
        return accountRepository.countQuantityNewCustomers(startDay, endDay);
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Account findByUserName(String userName) {
        return accountRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException(MessageConstant.ERROR_NOT_FOUND_ACCOUNT));
    }
}
