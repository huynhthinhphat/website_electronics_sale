package com.tip.b18.electronicsales.services.impls;

import com.fasterxml.jackson.databind.JsonNode;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.AccountDTO;
import com.tip.b18.electronicsales.dto.AccountLoginDTO;
import com.tip.b18.electronicsales.dto.AccountRegisterDTO;
import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.mappers.AccountMapper;
import com.tip.b18.electronicsales.services.AccountService;
import com.tip.b18.electronicsales.services.CartService;
import com.tip.b18.electronicsales.services.GoogleAuthService;
import com.tip.b18.electronicsales.utils.AccountUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final AccountService accountService;
    private final CartService cartService;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDTO authenticateWithGoogle(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = "https://people.googleapis.com/v1/people/me?personFields=names,emailAddresses,photos,birthdays,genders";

            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

            JsonNode body = response.getBody();
            if(!response.getStatusCode().is2xxSuccessful() || body == null){
                throw new IllegalArgumentException(MessageConstant.ERROR_INVALID_ACCESS_TOKEN);
            }

            String email = body.path("emailAddresses").path(0).path("value").asText(null);
            if(email == null || email.isBlank()){
                throw new NotFoundException(MessageConstant.ERROR_NOT_FOUND_EMAIL_FROM_SERVER);
            }

            String gender = body.path("genders").path(0).path("value").asText(null);

            Account account = accountService.findByEmail(email);
            if(account != null){
                return accountMapper.toDTO(account, cartService.getTotalQuantityItemInCartByAccountId(account.getId()));
            }

            AccountRegisterDTO accountRegisterDTO = accountMapper.toAccountRegister(body, email, gender != null ? getGender(gender) : null);
            accountService.registerAccount(accountRegisterDTO);

            return accountMapper.toDTO(accountService.findByEmail(email), 0);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public boolean getGender(String gender) {
        return gender.equals("male");
    }
}
