package com.ironhack.ironbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.ironbank.dto.*;
import com.ironhack.ironbank.model.AccountHolder;
import com.ironhack.ironbank.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper om;

    @Test
    void getAllUsers() throws Exception {
        var listOfUsersDto = List.of(
                new UserDtoResponse(),
                new UserDtoResponse(),
                new UserDtoResponse(),
                new UserDtoResponse()
        );
        when(userService.findAllUsers()).thenReturn(listOfUsersDto);

        mockMvc.perform(get("/users/admin/list-users")
                        .with(httpBasic("admin","admin")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*",hasSize(4)));
    }

    @Test
    void getAllAccountHolders() {
    }

    @Test
    void test_newAccountHolder() throws Exception {
        var userToCreate = new AccountHolderDtoRequest("Test User", "55555555D", "user1", LocalDate.parse("1986-04-16"), "user@test.com");
        var userCreated = new AccountHolder("Test User", "55555555D", "user1", "ROLE_USER", LocalDate.parse("1986-04-16"),"user@test.com");

        when(userService.newAccountHolder(userToCreate)).thenReturn(AccountHolderDtoResponse.fromAccountHolder(userCreated));

        mockMvc.perform(post("/users/ah/create")
                        .with(httpBasic("user","user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userToCreate)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("55555555D"));
    }

    @Test
    void test_newAdmin() throws Exception {
        var userToCreate = new AdminDtoRequest("Test Admin", "admin1", "admin1", "ROLE_USER,ROLE_ADMIN");
        var userCreated = new AdminDtoResponse("Test Admin", "admin1", "ROLE_USER,ROLE_ADMIN");

        when(userService.newAdmin(userToCreate)).thenReturn(userCreated);

        mockMvc.perform(post("/users/admin/create")
                        .with(httpBasic("admin","admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userToCreate)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("admin1"));
    }

    @Test
    void updateAH() {
    }

    @Test
    void updateAccHolderMainAddress() {
    }

    @Test
    void updateAccHolderMailingAddress() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void deleteUser() {
    }
}