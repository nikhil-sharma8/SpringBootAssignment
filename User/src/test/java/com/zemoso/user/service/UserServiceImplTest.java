package com.zemoso.user.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.zemoso.user.config.AccountServiceClient;
import com.zemoso.user.config.JWTService;
import com.zemoso.user.config.StockServiceClient;
import com.zemoso.user.dto.UserToken;
import com.zemoso.user.model.Account;
import com.zemoso.user.model.MyUserDetails;
import com.zemoso.user.model.Stock;
import com.zemoso.user.model.User;
import com.zemoso.user.repository.IUserRepository;
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
import java.util.Optional;

class UserServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private StockServiceClient stockServiceClient;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @Mock
    private UserToken token;

    @Mock
    private JWTService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testUser");
        testUser.setPassword("password");
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(accountServiceClient.getAccountsOfUser(testUser.getId())).thenReturn(List.of(new Account()));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userRepository).findAll();
        verify(accountServiceClient).getAccountsOfUser(testUser.getId());
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountServiceClient.getAccountsOfUser(testUser.getId())).thenReturn(List.of(new Account()));

        User user = userService.getUserById(testUser.getId());

        assertNotNull(user);
        assertEquals(testUser.getId(), user.getId());
        verify(userRepository).findById(testUser.getId());
        verify(accountServiceClient).getAccountsOfUser(testUser.getId());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());
        Long userId = testUser.getId();
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void testSaveUser() {
        testUser.setPassword("rawPassword");

        userService.saveUser(testUser);

        verify(userRepository).save(testUser);
        assertNotEquals("rawPassword", testUser.getPassword());
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(testUser.getId());

        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void testVerify_UserAuthenticated() {
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(service.generateToken(testUser.getName())).thenReturn("generated-jwt-token");
        when(token.getIsAuthenticated()).thenReturn(true);

        UserToken result = userService.verify(testUser);

        verify(token).setUsername(testUser.getName());
        verify(token).setIsAuthenticated(true);
        verify(token).setToken("generated-jwt-token");
        verify(token).setExpiration("30 minutes");

        assertEquals(token, result);
    }

    @Test
    void testVerify_UserNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        UserToken result = userService.verify(testUser);

        verify(token).setUsername(testUser.getName());
        verify(token).setIsAuthenticated(false);
        verify(token, never()).setToken(anyString());
        verify(token, never()).setExpiration(anyString());

        assertEquals(token, result);
    }

    @Test
    void testUpdateBalance_AccountNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountServiceClient.getAccountsOfUser(testUser.getId())).thenReturn(List.of(new Account(2L, "ABC", 100.0, 1L)));
        Long userId = testUser.getId();
        Exception exception = assertThrows(RuntimeException.class, () -> userService.updateBalance(userId, 100.0, 1L));

        assertEquals("Account Not Found", exception.getMessage());
    }

    @Test
    void testUpdateBalance_AccountUpdated() {
        Long userId = 1L;
        Double amount = 200.0;

        Account userAccount = new Account(1L, "XYZ", 500.0, 1L);
        User user = new User();
        user.setId(userId);
        user.setAccounts(List.of(userAccount));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountServiceClient.getAccountsOfUser(testUser.getId())).thenReturn(List.of(userAccount));
        doNothing().when(accountServiceClient).updateAccountInAccountService(any(Account.class));
        String result = userService.updateBalance(1L, amount, 1L);

        assertEquals("Account Updated", result);
        assertEquals(700.0, userAccount.getAmount());

        verify(userRepository, times(1)).save(user);

        verify(accountServiceClient, times(1)).updateAccountInAccountService(userAccount);
    }


    @Test
    void testBuyStock_BalanceSufficient() {
        Stock stock = new Stock();
        stock.setPrice(50.0);
        Account account = new Account(1L, "ABC", 100.0, 1L);

        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(stockServiceClient.getStockBySymbol(anyString())).thenReturn(stock);
        when(userRepository.findByName(anyString())).thenReturn(testUser);
        when(accountServiceClient.getAccountsOfUser(anyLong())).thenReturn(List.of(account));

        String result = userService.buyStock("AAPL", account.getId());

        assertEquals("Stock Bought", result);
        verify(accountServiceClient).updateAccountInAccountService(account);
        verify(userRepository).save(testUser);
    }

    @Test
    void testBuyStock_BalanceInSufficient() {
        Stock stock = new Stock();
        stock.setPrice(50.0);
        Account account = new Account(1L, "ABC", 10.0, 1L);

        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(stockServiceClient.getStockBySymbol(anyString())).thenReturn(stock);
        when(userRepository.findByName(anyString())).thenReturn(testUser);
        when(accountServiceClient.getAccountsOfUser(anyLong())).thenReturn(List.of(account));

        String result = userService.buyStock("ABC", 1L);
        assertEquals("Account balance not sufficient", result);
        verify(accountServiceClient, never()).updateAccountInAccountService(any(Account.class));
        verify(userRepository, never()).save(any(User.class));

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
    void testGetOwnedStocks() {
        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        testUser.setUserStocks(Collections.emptyList());
        when(userRepository.findByName(anyString())).thenReturn(testUser);

        List<Stock> stocks = userService.getOwnedStocks();
        assertNotNull(stocks);
        verify(userRepository, times(1)).findByName(anyString());
    }

    @Test
    void testSellStock() {
        Stock stock = new Stock();
        stock.setPrice(50.0);
        Account account = new Account(1L, "ABC", 100.0, 1L);

        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(stockServiceClient.getStockBySymbol(anyString())).thenReturn(stock);
        when(userRepository.findByName(anyString())).thenReturn(testUser);
        when(accountServiceClient.getAccountsOfUser(anyLong())).thenReturn(List.of(account));

        String result = userService.sellStock("AAPL", account.getId());

        assertEquals("Stock Sold", result);
        verify(accountServiceClient).updateAccountInAccountService(account);
        verify(userRepository).save(testUser);
    }

    @Test
    void testGetUserData() {
        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String username = userService.getUserData();

        assertEquals("testUser", username);
    }
}

