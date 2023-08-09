package com.ironhack.ironbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.ironbank.dto.AccountDto;
import com.ironhack.ironbank.service.AccountService;
import com.ironhack.ironbank.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper om;

    @Test
    void getMyAccounts() throws Exception {
        var listOfAccountsDto = List.of(
                new AccountDto(),
                new AccountDto(),
                new AccountDto(),
                new AccountDto()
        );
        when(userService.getAccountsByUserLogged()).thenReturn(listOfAccountsDto);

        mockMvc.perform(get("/accounts/list")
                        .with(httpBasic("user","user")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*",hasSize(4)));
    }

    @Test
    void newAccount() {
    }

    @Test
    void editSecondaryOwner() {
    }

    @Test
    void editBalance() {
    }

    @Test
    void editAccountConditions() {
    }

    @Test
    void deleteAccount() {
    }
}