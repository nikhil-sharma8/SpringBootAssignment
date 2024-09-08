package com.zemoso.Stocks.controller;

import com.zemoso.Stocks.model.Stock;
import com.zemoso.Stocks.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

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
    public void testGetAllStocks() throws Exception {
        List<Stock> stocks = Collections.singletonList(stock);

        when(stockService.getAllStocks()).thenReturn(stocks);

        mockMvc.perform(get("/api/v1/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[0].name").value("Apple Inc."));

        verify(stockService, times(1)).getAllStocks();
    }

    @Test
    public void testGetStockBySymbol() throws Exception {
        when(stockService.getStockBySymbol("AAPL")).thenReturn(stock);

        mockMvc.perform(get("/api/v1/stock/AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.name").value("Apple Inc."));

        verify(stockService, times(1)).getStockBySymbol("AAPL");
    }

    @Test
    public void testSaveStock() throws Exception {
        when(stockService.saveStock(any(Stock.class))).thenReturn("Stock saved!");

        mockMvc.perform(post("/api/v1/stock")
                        .contentType("application/json")
                        .content("{ \"symbol\": \"AAPL\", \"name\": \"Apple Inc.\", \"price\": 150.00, \"volume\": 1000000 }"))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock saved!"));

        verify(stockService, times(1)).saveStock(any(Stock.class));
    }

    @Test
    public void testDeleteStock() throws Exception {
        when(stockService.deleteStock(1L)).thenReturn("Stock deleted!");

        mockMvc.perform(delete("/api/v1/stock/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock deleted!"));

        verify(stockService, times(1)).deleteStock(1L);
    }
}
