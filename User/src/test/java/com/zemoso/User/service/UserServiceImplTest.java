package com.zemoso.User.service;

import com.zemoso.User.dto.UserToken;
import com.zemoso.User.model.Account;
import com.zemoso.User.model.MyUserDetails;
import com.zemoso.User.model.Stock;
import com.zemoso.User.model.User;
import com.zemoso.User.repository.iUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private UserToken token;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        MyUserDetails myUserDetails = mock(MyUserDetails.class);

        when(myUserDetails.getUsername()).thenReturn(username);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        users.add(user);

        when(userRepository.findAll()).thenReturn(users);
        when(accountServiceClient.getAccountsOfUser(user.getId())).thenReturn(new ArrayList<>());

        List<User> result = userService.getAllUsers();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountServiceClient.getAccountsOfUser(user.getId())).thenReturn(new ArrayList<>());

        User result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setPassword("password");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            assertTrue(passwordEncoder.matches("password",u.getPassword()));
            return null;
        }).when(userRepository).save(any(User.class));

        String result = userService.saveUser(user);
        assertEquals("User saved", result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        String result = userService.deleteUser(1L);
        assertEquals("User deleted", result);
        verify(userRepository, times(1)).deleteById(1L);
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

        UserToken resultToken = new UserToken();
        resultToken.setIsAuthenticated(true);
        resultToken.setToken("jwtToken");
        resultToken.setExpiration("30 minutes");

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
        user.setId(userId);
        Account account = new Account(accountId, "123", 200.0, userId);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        user.setAccounts(accounts);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(accountServiceClient).updateAccountInAccountService(any(Account.class));

        String result = userService.updateBalance(userId, amount, accountId);
        assertEquals("Account Updated", result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetOwnedStocks() {
        mockSecurityContext("username");

        User user = new User();
        user.setName("username");
        user.setUserStocks(new ArrayList<>());

        when(userRepository.findByName("username")).thenReturn(user);

        List<Stock> result = userService.getOwnedStocks();
        assertNotNull(result);
        verify(userRepository, times(1)).findByName("username");
    }

    @Test
    void testBuyStock() {
        mockSecurityContext("username");

        String stockSymbol = "AAPL";
        Long accountId = 1L;

        Stock stock = new Stock();
        stock.setPrice(100.0);
        when(stockServiceClient.getStockBySymbol(stockSymbol)).thenReturn(stock);

        User user = new User();
        Account account = new Account(accountId, "123", 200.0, 1L);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        user.setAccounts(accounts);
        user.setUserStocks(new ArrayList<>());

        when(userRepository.findByName(anyString())).thenReturn(user);

        String result = userService.buyStock(stockSymbol, accountId);
        assertEquals("Stock Bought", result);
        verify(userRepository, times(1)).save(user);
        verify(accountServiceClient, times(1)).updateAccountInAccountService(account);
    }

    @Test
    void testSellStock() {
        mockSecurityContext("username");

        String stockSymbol = "AAPL";
        Long accountId = 1L;

        Stock stock = new Stock();
        stock.setPrice(100.0);
        when(stockServiceClient.getStockBySymbol(stockSymbol)).thenReturn(stock);

        User user = new User();
        Account account = new Account(accountId, "123", 200.0, 1L);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        user.setAccounts(accounts);
        user.setUserStocks(new ArrayList<>());

        when(userRepository.findByName(anyString())).thenReturn(user);

        String result = userService.sellStock(stockSymbol, accountId);
        assertEquals("Stock Sold", result);
        verify(userRepository, times(1)).save(user);
        verify(accountServiceClient, times(1)).updateAccountInAccountService(account);
    }
}
