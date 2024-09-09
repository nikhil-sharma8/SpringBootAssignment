package com.zemoso.user.service;

import com.zemoso.user.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AccountServiceClient {

    WebClient.Builder webClientBuilder;

    AccountServiceClient(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;
    }

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
