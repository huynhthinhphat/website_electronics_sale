package com.tip.b18.electronicsales.controllers;

import com.tip.b18.electronicsales.constants.EndPointConstants;
import com.tip.b18.electronicsales.dto.AccountDTO;
import com.tip.b18.electronicsales.dto.CustomPage;
import com.tip.b18.electronicsales.dto.PageInfoDTO;
import com.tip.b18.electronicsales.exceptions.GlobalExceptionHandler;
import com.tip.b18.electronicsales.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private List<AccountDTO> accountsDTOList;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        accountsDTOList = new ArrayList<>();
    }

    @Test
    void viewAccounts_WithValidParams_ReturnsPagedAccounts() throws Exception {
        AccountDTO accountFirst = new AccountDTO();
        accountFirst.setUserName("htphat");

        AccountDTO accountSecond = new AccountDTO();
        accountSecond.setUserName("inter");

        accountsDTOList.add(accountFirst);
        accountsDTOList.add(accountSecond);

        CustomPage<AccountDTO> customPage = new CustomPage<>();
        customPage.setPageInfo(new PageInfoDTO(2, 2));
        customPage.setItems(accountsDTOList);

        when(accountService.viewAccounts("", 0, 2)).thenReturn(customPage);

        mockMvc.perform(get(EndPointConstants.END_POINT_ACCOUNTS)
                        .param("search", "")
                        .param("page", "0")
                        .param("limit", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.items[0].userName").value("htphat"))
                .andExpect(jsonPath("$.data.items[1].userName").value("inter"))
                .andExpect(jsonPath("$.data.pageInfo").value(new PageInfoDTO(2, 2)));
    }

    @Test
    void viewAccounts_WhenSearchingByPhoneNumber_ReturnsPagedAccounts() throws Exception {
        AccountDTO accountFirst = new AccountDTO();
        accountFirst.setUserName("inter");
        accountFirst.setPhoneNumber("0375204599");
        accountsDTOList.add(accountFirst);

        CustomPage<AccountDTO> customPage = new CustomPage<>();
        customPage.setPageInfo(new PageInfoDTO(1, 1));
        customPage.setItems(accountsDTOList);

        when(accountService.viewAccounts("0375204599", 0, 2)).thenReturn(customPage);

        mockMvc.perform(get(EndPointConstants.END_POINT_ACCOUNTS)
                        .param("search", "0375204599")
                        .param("page", "0")
                        .param("limit", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.items[0].userName").value("inter"))
                .andExpect(jsonPath("$.data.pageInfo").value(new PageInfoDTO(1, 1)));;
    }

    @Test
    void viewAccounts_WhenSearchingByUserName_ReturnsPagedAccounts() throws Exception {
        AccountDTO accountFirst = new AccountDTO();
        accountFirst.setUserName("inter");

        AccountDTO accountSecond = new AccountDTO();
        accountSecond.setUserName("internABC");

        CustomPage<AccountDTO> customPage = new CustomPage<>();
        customPage.setPageInfo(new PageInfoDTO(2, 1));
        customPage.setItems(List.of(accountFirst, accountSecond));

        when(accountService.viewAccounts("inte", 0, 2)).thenReturn(customPage);

        mockMvc.perform(get(EndPointConstants.END_POINT_ACCOUNTS)
                        .param("search", "inte")
                        .param("page", "0")
                        .param("limit", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.items[0].userName").value("inter"))
                .andExpect(jsonPath("$.data.items[1].userName").value("internABC"))
                .andExpect(jsonPath("$.data.pageInfo").value(new PageInfoDTO(2, 1)));
    }

    @Test
    void viewAccounts_WhenLimitIsZero_ReturnsEmptyPage() throws Exception {
        CustomPage<AccountDTO> customPage = new CustomPage<>();
        customPage.setPageInfo(new PageInfoDTO(0, 0));
        customPage.setItems(accountsDTOList);

        when(accountService.viewAccounts("inte", 0, 5)).thenReturn(customPage);

        mockMvc.perform(get(EndPointConstants.END_POINT_ACCOUNTS)
                        .param("search", "inte")
                        .param("page", "0")
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.items").value(accountsDTOList));
    }

    @Test
    void viewAccounts_WhenPageIsZero_ReturnsPagedAccounts() throws Exception {
        AccountDTO accountFirst = new AccountDTO();
        accountFirst.setUserName("inter");

        AccountDTO accountSecond = new AccountDTO();
        accountSecond.setUserName("internABC");

        CustomPage<AccountDTO> customPage = new CustomPage<>();
        customPage.setPageInfo(new PageInfoDTO(0, 5));
        customPage.setItems(List.of(accountFirst, accountSecond));

        when(accountService.viewAccounts("inte", 0, 5)).thenReturn(customPage);

        mockMvc.perform(get(EndPointConstants.END_POINT_ACCOUNTS)
                        .param("search", "inte")
                        .param("page", "0")
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.items[0].userName").value("inter"))
                .andExpect(jsonPath("$.data.items[1].userName").value("internABC"))
                .andExpect(jsonPath("$.data.pageInfo").value(new PageInfoDTO(0, 5)));
    }
}
