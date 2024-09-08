package com.zemoso.User.service;

import com.zemoso.User.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AccountServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public List<Account> getAccountsOfUser(Long userId) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/api/v1/account/user/{userId}", userId)
                .retrieve()
                .bodyToFlux(Account.class)
                .collectList()
                .block();
    }

    public void updateAccountInAccountService(Account account) {
        webClientBuilder.build()
                .put()
                .uri("http://localhost:8080/api/v1/account")
                .bodyValue(account)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
