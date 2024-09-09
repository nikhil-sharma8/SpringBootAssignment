package com.zemoso.stock.service;

import com.zemoso.stock.model.Stock;
import com.zemoso.stock.repository.IStockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    IStockRepository stockRepository;

    StockServiceImpl(IStockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

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
