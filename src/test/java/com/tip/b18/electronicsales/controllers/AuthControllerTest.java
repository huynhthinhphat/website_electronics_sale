package com.tip.b18.electronicsales.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tip.b18.electronicsales.constants.EndPointConstants;
import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.AccountDTO;
import com.tip.b18.electronicsales.dto.AccountLoginDTO;
import com.tip.b18.electronicsales.dto.AccountRegisterDTO;
import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.exceptions.AlreadyExistsException;
import com.tip.b18.electronicsales.exceptions.CredentialsException;
import com.tip.b18.electronicsales.exceptions.GlobalExceptionHandler;
import com.tip.b18.electronicsales.services.AccountService;
import com.tip.b18.electronicsales.services.JwtService;
import com.tip.b18.electronicsales.services.impls.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private static UUID userId;
    private JwtService jwtServiceMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        this.objectMapper = new ObjectMapper();
        userId = UUID.randomUUID();
        jwtServiceMock = new JwtServiceImpl(System.getenv("key_secret"));
    }

    @Test
    void loginAccount_ValidCredentials_ReturnsSuccessResponseWithToken() throws Exception {
        AccountLoginDTO accountLoginDTO = new AccountLoginDTO("htphat", "123");
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(userId);
        accountDTO.setUserName("htphat");
        accountDTO.setRole(false);

        String token = jwtServiceMock.generateToken("htphat", userId, false);

        when(accountService.loginAccount(accountLoginDTO)).thenReturn(accountDTO);
        when(jwtService.generateToken("htphat", userId, false)).thenReturn(token);

        MvcResult result = mockMvc.perform(post(EndPointConstants.END_POINT_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value(MessageConstant.SUCCESS_ACCOUNT_LOGGED_IN))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.data.userName").value("htphat"))
                .andExpect(jsonPath("$.data.role").value(false))
                .andReturn();

        verifyToken(result.getResponse().getContentAsString(), jwtServiceMock);
        verify(accountService).loginAccount(accountLoginDTO);
    }

    private void verifyToken(String responseBody, JwtService jwtServiceMock) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Claims claims = jwtServiceMock.extractClaims(jsonNode.get("token").asText());
        boolean role = !claims.get("role", String.class).equals("USER");
        String id = claims.get("id", String.class);
        String userName = claims.getSubject();

        JsonNode dataNode = jsonNode.get("data");
        assertEquals(id, dataNode.get("id").asText());
        assertEquals(role, dataNode.get("role").asBoolean());
        assertEquals(userName, dataNode.get("userName").asText());
    }

    @Test
    void loginAccount_EmptyCredentials_ReturnsErrorResponse() throws Exception {
        AccountLoginDTO accountLoginDTO = new AccountLoginDTO("", "123");
        mockMvc.perform(post(EndPointConstants.END_POINT_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountLoginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(MessageConstant.ERROR_VALUE_REQUIRED));
    }

    @Test
    void loginAccount_InvalidCredentials_ReturnsErrorResponse() throws Exception {
        AccountLoginDTO accountLoginDTO = new AccountLoginDTO("wrong-username", "123");
        when(accountService.loginAccount(accountLoginDTO)).thenThrow(new CredentialsException(MessageConstant.ERROR_INVALID_CREDENTIALS));

        mockMvc.perform(post(EndPointConstants.END_POINT_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountLoginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(MessageConstant.ERROR_INVALID_CREDENTIALS));
    }

    @Test
    void registerAccount_ValidCredentials_ReturnsSuccessResponse() throws Exception {
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        accountRegisterDTO.setUserName("htphat");
        accountRegisterDTO.setPassword("123");
        accountRegisterDTO.setFullName("Huỳnh Thịnh Phát");

        doNothing().when(accountService).registerAccount(accountRegisterDTO);

        mockMvc.perform(post(EndPointConstants.END_POINT_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRegisterDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value(MessageConstant.SUCCESS_ACCOUNT_REGISTERED))
                .andReturn();
    }

    @Test
    void registerAccount_EmptyCredentials_ReturnsErrorResponse() throws Exception {
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        accountRegisterDTO.setUserName("");
        accountRegisterDTO.setPassword("");
        accountRegisterDTO.setFullName("");

        mockMvc.perform(post(EndPointConstants.END_POINT_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRegisterDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(MessageConstant.ERROR_VALUE_REQUIRED));
    }

    @Test
    void registerAccount_ExistingUsername_ReturnsErrorResponse() throws Exception {
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        accountRegisterDTO.setUserName("htphat");
        accountRegisterDTO.setPassword("123");
        accountRegisterDTO.setFullName("Huỳnh Thịnh Phát");

        doThrow(new AlreadyExistsException(MessageConstant.ERROR_ACCOUNT_EXISTS))
                .when(accountService).registerAccount(accountRegisterDTO);

        mockMvc.perform(post(EndPointConstants.END_POINT_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRegisterDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(MessageConstant.ERROR_ACCOUNT_EXISTS));
    }
}
