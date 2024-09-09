package com.zemoso.User.controller;

import com.zemoso.User.model.Stock;
import com.zemoso.User.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    @Autowired
    StockService userService;

    @GetMapping("/owned/")
    public List<Stock> getOwnedStocks() {
        return userService.getOwnedStocks();
    }

    @GetMapping
    public List<Stock> getFullStockList() {
        return userService.getFullStockList();
    }

    @PostMapping("/buy")
    public String buyStock(@RequestParam String stockSymbol, @RequestParam Long accountId) {
        return userService.buyStock(stockSymbol, accountId);
    }

    @DeleteMapping("/sell")
    public String sellStock(@RequestParam String stockSymbol, @RequestParam Long accountId) {
        return userService.sellStock(stockSymbol, accountId);
    }
}
