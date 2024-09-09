package com.zemoso.account.controller;

import com.zemoso.account.model.Account;
import com.zemoso.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account account;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        account = new Account(1L, "ACC-1234", 1000.00);
        account.setUserId(1L);
    }

    @Test
    void testGetAllAccounts() throws Exception {
        when(accountService.getAllAccount()).thenReturn(Collections.singletonList(account));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountNumber").value("ACC-1234"));

        verify(accountService, times(1)).getAllAccount();
    }

    @Test
    void testGetAccountById() throws Exception {
        when(accountService.getAccountById(anyLong())).thenReturn(account);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("ACC-1234"));

        verify(accountService, times(1)).getAccountById(1L);
    }

    @Test
    void testGetAccountById_NotFound() throws Exception {
        when(accountService.getAccountById(anyLong())).thenThrow(new RuntimeException("Account not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Account not found"));

        verify(accountService, times(1)).getAccountById(1L);
    }

    @Test
    void testSaveAccount() throws Exception {
        when(accountService.saveAccount(any(Account.class))).thenReturn("Account Saved");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"accountNumber\":\"ACC-1234\",\"amount\":1000.00,\"userId\":1}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Account Saved"));

        verify(accountService, times(1)).saveAccount(any(Account.class));
    }

    @Test
    void testDeleteAccount() throws Exception {
        when(accountService.deleteAccount(anyLong())).thenReturn("Account Deleted");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/account/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Account Deleted"));

        verify(accountService, times(1)).deleteAccount(1L);
    }

    @Test
    void testGetAccountsByUserId() throws Exception {
        when(accountService.getAccountsByUserId(anyLong())).thenReturn(Collections.singletonList(account));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountNumber").value("ACC-1234"));

        verify(accountService, times(1)).getAccountsByUserId(1L);
    }

    @Test
    void testUpdateAccount() throws Exception {
        when(accountService.updateAccount(any(Account.class))).thenReturn("Account Updated");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"accountNumber\":\"ACC-1234\",\"amount\":1200.00,\"userId\":1}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Account Updated"));

        verify(accountService, times(1)).updateAccount(any(Account.class));
    }
}
