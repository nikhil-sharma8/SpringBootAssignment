package com.zemoso.Stocks.service;

import com.zemoso.Stocks.model.Stock;

import java.util.List;

public interface StockService {

    List<Stock> getAllStocks();

    String saveStock(Stock stock);

    Stock getStockBySymbol(String symbol);

    String deleteStock(Long id);
}
