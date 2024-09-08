package com.zemoso.Stocks.controller;

import com.zemoso.Stocks.model.Stock;
import com.zemoso.Stocks.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {

    @Autowired
    StockService stockService;

    @GetMapping
    public List<Stock> getAllStock(){
        return stockService.getAllStocks();
    }

    @GetMapping("/{symbol}")
    public Stock getStockById(@PathVariable String symbol){
        return stockService.getStockBySymbol(symbol);
    }

    @PostMapping
    public String saveStock(@RequestBody Stock stock){
        return stockService.saveStock(stock);
    }

    @DeleteMapping("/{id}")
    public String deleteStock(@PathVariable Long id){
        return stockService.deleteStock(id);
    }
}
