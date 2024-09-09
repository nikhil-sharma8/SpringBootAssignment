package com.zemoso.account.service;

import com.zemoso.account.model.Account;
import com.zemoso.account.repository.IAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{

    IAccountRepository accountRepository;

    AccountServiceImpl(IAccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccount(){
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id){
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public String saveAccount(Account account){
        accountRepository.save(account);
        return "Account Saved";
    }

    public String deleteAccount(Long id){
        accountRepository.deleteById(id);
        return "Account Deleted";
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    @Override
    public String updateAccount(Account account) {

        Account existingAccount = accountRepository.findById(account.getId()).orElseThrow(() -> new RuntimeException("Account not found"));

        existingAccount.setAmount(account.getAmount());

        accountRepository.save(existingAccount);

        return "Account Updated";
    }
}
