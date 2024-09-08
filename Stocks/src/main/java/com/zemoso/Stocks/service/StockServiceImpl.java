package com.zemoso.Stocks.service;

import com.zemoso.Stocks.model.Stock;
import com.zemoso.Stocks.repository.iStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService{

    @Autowired
    iStockRepository stockRepository;

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public String saveStock(Stock stock) {
        stockRepository.save(stock);
        return "Stock saved!";
    }

    @Override
    public Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol);
    }

    @Override
    public String deleteStock(Long id) {
        stockRepository.deleteById(id);
        return "Stock deleted!";
    }
}
