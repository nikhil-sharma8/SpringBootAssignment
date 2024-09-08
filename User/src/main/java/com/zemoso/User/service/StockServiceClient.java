package com.zemoso.User.service;

import com.zemoso.User.model.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class StockServiceClient {
    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String STOCK_SERVICE_BASE_URL = "http://localhost:8082/api/v1/stock";

    public List<Stock> getAllStocks() {
        return webClientBuilder.build()
                .get()
                .uri(STOCK_SERVICE_BASE_URL)
                .retrieve()
                .bodyToFlux(Stock.class)
                .collectList()
                .block();
    }

    public Stock getStockBySymbol(String symbol) {
        return webClientBuilder.build()
                .get()
                .uri(STOCK_SERVICE_BASE_URL + "/" + symbol)
                .retrieve()
                .bodyToMono(Stock.class)
                .block();
    }
}
