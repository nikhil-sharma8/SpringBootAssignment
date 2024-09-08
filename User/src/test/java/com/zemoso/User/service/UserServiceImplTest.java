package com.zemoso.User.service;

import com.zemoso.User.dto.UserToken;
import com.zemoso.User.model.Account;
import com.zemoso.User.model.Stock;
import com.zemoso.User.model.User;
import com.zemoso.User.repository.iUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private iUserRepository userRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private StockServiceClient stockServiceClient;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserToken userToken;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(accountServiceClient.getAccountsOfUser(user.getId())).thenReturn(Collections.emptyList());

        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        User user = new User();
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));
        when(accountServiceClient.getAccountsOfUser(user.getId())).thenReturn(Collections.emptyList());

        User foundUser = userService.getUserById(user.getId());
        assertNotNull(foundUser);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testSaveUser() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        String response = userService.saveUser(user);
        assertEquals("User saved", response);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        String response = userService.deleteUser(userId);
        assertEquals("User deleted", response);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testVerifyUser() {
        User user = new User();
        user.setName("username");
        user.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(jwtService.generateToken(user.getName())).thenReturn("jwtToken");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserToken token = userService.verify(user);

        assertTrue(token.getIsAuthenticated());
        assertEquals("jwtToken", token.getToken());
        assertEquals("30 minutes", token.getExpiration());
    }

    @Test
    void testUpdateBalance() {
        Long userId = 1L;
        Double amount = 100.0;
        Long accountId = 1L;
        User user = new User();
        Account account = new Account();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        doNothing().when(accountServiceClient).updateAccountInAccountService(any(Account.class));

        String response = userService.updateBalance(userId, amount, accountId);
        assertEquals("Account Updated", response);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetOwnedStocks() {
        User user = new User();
        user.setUserStocks(Collections.emptyList());
        when(userRepository.findByName(anyString())).thenReturn(user);

        List<Stock> stocks = userService.getOwnedStocks();
        assertNotNull(stocks);
        verify(userRepository, times(1)).findByName(anyString());
    }

    @Test
    void testGetFullStockList() {
        List<Stock> stocks = Collections.singletonList(new Stock());
        when(stockServiceClient.getAllStocks()).thenReturn(stocks);

        List<Stock> stockList = userService.getFullStockList();
        assertNotNull(stockList);
        assertEquals(1, stockList.size());
        verify(stockServiceClient, times(1)).getAllStocks();
    }

    @Test
    void testBuyStock() {
        String stockSymbol = "AAPL";
        Long accountId = 1L;
        Stock stock = new Stock();
        stock.setPrice(150.0);
        User user = new User();
        Account account = new Account();
        account.setAmount(200.0);
        user.setAccounts(List.of(account));
        when(stockServiceClient.getStockBySymbol(stockSymbol)).thenReturn(stock);
        when(userRepository.findByName(anyString())).thenReturn(user);
        doNothing().when(accountServiceClient).updateAccountInAccountService(any(Account.class));

        String response = userService.buyStock(stockSymbol, accountId);
        assertEquals("Stock Bought", response);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSellStock() {
        String stockSymbol = "AAPL";
        Long accountId = 1L;
        Stock stock = new Stock();
        stock.setPrice(150.0);
        User user = new User();
        Account account = new Account();
        account.setAmount(100.0);
        user.setAccounts(List.of(account));
        when(stockServiceClient.getStockBySymbol(stockSymbol)).thenReturn(stock);
        when(userRepository.findByName(anyString())).thenReturn(user);
        doNothing().when(accountServiceClient).updateAccountInAccountService(any(Account.class));

        String response = userService.sellStock(stockSymbol, accountId);
        assertEquals("Stock Sold", response);
        verify(userRepository, times(1)).save(user);
    }
}

