package com.zemoso.Account.controller;

import com.zemoso.Account.model.Account;
import com.zemoso.Account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping
    public List<Account> getAllAccounts(){
        return accountService.getAllAccount();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        try {
            Account account = accountService.getAccountById(id);
            return ResponseEntity.ok(account);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping
    public String saveAccount(@RequestBody Account account){
        return accountService.saveAccount(account);
    }

    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id){
        return accountService.deleteAccount(id);
    }

    @GetMapping("/user/{userId}")
    public List<Account> getAccountsByUserId(@PathVariable Long userId){
        return accountService.getAccountsByUserId(userId);
    }

    @PutMapping
    public String updateAccount(@RequestBody Account account){
        return accountService.updateAccount(account);
    }
}
