package com.zemoso.stock.service;

import com.zemoso.stock.model.Stock;
import com.zemoso.stock.repository.IStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockServiceImplTest {

    @Mock
    private IStockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        stock = new Stock();
        stock.setId(1L);
        stock.setSymbol("AAPL");
        stock.setName("Apple Inc.");
        stock.setPrice(150.00);
        stock.setVolume(1000000);
        stock.setDate(LocalDate.now());
    }

    @Test
    public void testGetAllStocks() {
        when(stockRepository.findAll()).thenReturn(Collections.singletonList(stock));

        assertEquals(1, stockService.getAllStocks().size());
        assertEquals("AAPL", stockService.getAllStocks().get(0).getSymbol());

        verify(stockRepository, times(2)).findAll();
    }

    @Test
    public void testSaveStock() {
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        String result = stockService.saveStock(stock);

        assertEquals("Stock saved!", result);
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    public void testGetStockBySymbol() {
        when(stockRepository.findBySymbol("AAPL")).thenReturn(stock);

        Stock result = stockService.getStockBySymbol("AAPL");

        assertEquals("AAPL", result.getSymbol());
        verify(stockRepository, times(1)).findBySymbol("AAPL");
    }

    @Test
    public void testDeleteStock() {
        doNothing().when(stockRepository).deleteById(1L);

        String result = stockService.deleteStock(1L);

        assertEquals("Stock deleted!", result);
        verify(stockRepository, times(1)).deleteById(1L);
    }
}
