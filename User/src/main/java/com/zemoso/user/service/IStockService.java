package com.zemoso.user.service;

import com.zemoso.user.model.Stock;

import java.util.List;

public interface IStockService {
    List<Stock> getOwnedStocks();

    List<Stock> getFullStockList();

    String buyStock(String stockSymbol, Long accountId);

    String sellStock(String stockSymbol, Long accountId);
}
