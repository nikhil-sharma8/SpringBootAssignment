package com.zemoso.stock.repository;

import com.zemoso.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IStockRepository extends JpaRepository<Stock, Long> {

    Stock findBySymbol(String symbol);
}
