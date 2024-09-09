package com.zemoso.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zemoso.user.model.Stock;
import com.zemoso.user.service.IStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
class StockControllerTest {

    private MockMvc mockMvc;

    private List<Stock> stockList = new ArrayList<>();

    @InjectMocks
    StockController stockController;

    ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    IStockService stockService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).build();
        stockList = Arrays.asList(
                new Stock(1L, "AAPL", "Apple", 150.0, 5000, null),
                new Stock(2L, "GOOGL", "Google", 2800.0, 3000, null)
        );
    }

    @Test
    void testGetOwnedStocks() throws Exception {
        when(stockService.getOwnedStocks()).thenReturn(stockList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stocks/owned/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(stockList)));
    }

    @Test
    void testGetFullStockList() throws Exception {
        when(stockService.getFullStockList()).thenReturn(stockList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(stockList)));
    }

    @Test
    void testBuyStock() throws Exception {
        String stockSymbol = "AAPL";
        long accountId = 1L;
        when(stockService.buyStock(anyString(), anyLong())).thenReturn("Stock purchased");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stocks/buy")
                        .param("stockSymbol", stockSymbol)
                        .param("accountId", Long.toString(accountId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Stock purchased"));
    }

    @Test
    void testSellStock() throws Exception {
        String stockSymbol = "AAPL";
        long accountId = 1L;
        when(stockService.sellStock(anyString(), anyLong())).thenReturn("Stock sold");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/stocks/sell")
                        .param("stockSymbol", stockSymbol)
                        .param("accountId", Long.toString(accountId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Stock sold"));
    }

}
