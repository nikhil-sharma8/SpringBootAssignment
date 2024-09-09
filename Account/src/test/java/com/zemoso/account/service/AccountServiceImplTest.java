package com.zemoso.account.service;

import com.zemoso.account.model.Account;
import com.zemoso.account.repository.iAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    @Mock
    private iAccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        account = new Account(1L, "ACC-1234", 1000.00);
        account.setUserId(1L);
    }

    @Test
    public void testGetAllAccount() {
        when(accountRepository.findAll()).thenReturn(Collections.singletonList(account));

        assertEquals(1, accountService.getAllAccount().size());
        assertEquals("ACC-1234", accountService.getAllAccount().get(0).getAccountNumber());

        verify(accountRepository, times(2)).findAll();
    }

    @Test
    public void testGetAccountById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById(1L);

        assertEquals("ACC-1234", result.getAccountNumber());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAccountById_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> accountService.getAccountById(1L));
        assertEquals("Account not found", thrown.getMessage());

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    public void testSaveAccount() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        String result = accountService.saveAccount(account);

        assertEquals("Account Saved", result);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testDeleteAccount() {
        doNothing().when(accountRepository).deleteById(1L);

        String result = accountService.deleteAccount(1L);

        assertEquals("Account Deleted", result);
        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetAccountsByUserId() {
        when(accountRepository.findByUserId(1L)).thenReturn(Collections.singletonList(account));

        assertEquals(1, accountService.getAccountsByUserId(1L).size());
        assertEquals("ACC-1234", accountService.getAccountsByUserId(1L).get(0).getAccountNumber());

        verify(accountRepository, times(2)).findByUserId(1L);
    }

    @Test
    public void testUpdateAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        String result = accountService.updateAccount(account);

        assertEquals("Account Updated", result);
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testUpdateAccount_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> accountService.updateAccount(account));
        assertEquals("Account not found", thrown.getMessage());

        verify(accountRepository, times(1)).findById(1L);
    }
}
