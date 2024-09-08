package com.zemoso.Account.service;

import com.zemoso.Account.model.Account;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccount();

    String saveAccount(Account account);

    Account getAccountById(Long id);

    String deleteAccount(Long id);

    List<Account> getAccountsByUserId(Long userId);

    String updateAccount(Account account);
}
