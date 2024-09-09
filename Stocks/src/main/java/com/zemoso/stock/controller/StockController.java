package com.zemoso.stock.controller;

import com.zemoso.stock.model.Stock;
import com.zemoso.stock.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {

    StockService stockService;

    StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<Stock> getAllStock() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{symbol}")
    public Stock getStockById(@PathVariable String symbol) {
        return stockService.getStockBySymbol(symbol);
    }

    @PostMapping
    public String saveStock(@RequestBody Stock stock) {
        return stockService.saveStock(stock);
    }

    @DeleteMapping("/{id}")
    public String deleteStock(@PathVariable Long id) {
        return stockService.deleteStock(id);
    }
}
