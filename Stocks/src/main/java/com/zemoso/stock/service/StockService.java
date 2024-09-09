package com.zemoso.stock.service;

import com.zemoso.stock.model.Stock;

import java.util.List;

public interface StockService {

    List<Stock> getAllStocks();

    String saveStock(Stock stock);

    Stock getStockBySymbol(String symbol);

    String deleteStock(Long id);
}
