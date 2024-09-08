package com.zemoso.User.service;

import com.zemoso.User.model.Stock;

import java.util.List;

public interface StockService {
    List<Stock> getOwnedStocks();

    List<Stock> getFullStockList();

    String buyStock(String stockSymbol, Long accountId);

    String sellStock(String stockSymbol, Long accountId);
}
