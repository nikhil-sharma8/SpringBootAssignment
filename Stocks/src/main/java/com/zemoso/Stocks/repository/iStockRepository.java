package com.zemoso.Stocks.repository;

import com.zemoso.Stocks.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface iStockRepository extends JpaRepository<Stock, Long> {

    Stock findBySymbol(String symbol);
}
