package com.zemoso.user.controller;

import com.zemoso.user.model.Stock;
import com.zemoso.user.service.IStockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    IStockService stockService;

    StockController(IStockService stockService){
        this.stockService = stockService;
    }

    @GetMapping("/owned/")
    public List<Stock> getOwnedStocks() {
        return stockService.getOwnedStocks();
    }

    @GetMapping
    public List<Stock> getFullStockList() {
        return stockService.getFullStockList();
    }

    @PostMapping("/buy")
    public String buyStock(@RequestParam String stockSymbol, @RequestParam Long accountId) {
        return stockService.buyStock(stockSymbol, accountId);
    }

    @DeleteMapping("/sell")
    public String sellStock(@RequestParam String stockSymbol, @RequestParam Long accountId) {
        return stockService.sellStock(stockSymbol, accountId);
    }
}
