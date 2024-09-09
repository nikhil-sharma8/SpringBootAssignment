package com.zemoso.user.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.zemoso.user.dto.UserToken;
import com.zemoso.user.model.Account;
import com.zemoso.user.model.MyUserDetails;
import com.zemoso.user.model.Stock;
import com.zemoso.user.model.User;
import com.zemoso.user.repository.IUserRepository;
import com.zemoso.user.service.AccountServiceClient;
import com.zemoso.user.service.JWTService;
import com.zemoso.user.service.StockServiceClient;
import com.zemoso.user.service.UserServiceImpl;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

class UserServiceImplTest {

    @Mock
    IUserRepository userRepository;

    @Mock
    AccountServiceClient accountServiceClient;

    @Mock
    StockServiceClient stockServiceClient;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JWTService jwtService;

    @Mock
    UserToken userToken;

    @InjectMocks
    UserServiceImpl userService;

    private User testUser;

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
        verify(userRepository, times(1)).findAll();
        verify(accountServiceClient, times(1)).getAccountsOfUser(testUser.getId());
    }

    @Test
    void testGetUserById_UserExists() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountServiceClient.getAccountsOfUser(testUser.getId())).thenReturn(List.of(new Account()));

        User user = userService.getUserById(testUser.getId());

        assertNotNull(user);
        assertEquals(testUser.getId(), user.getId());
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(accountServiceClient, times(1)).getAccountsOfUser(testUser.getId());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(testUser.getId());
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(testUser.getId());
    }

    @Test
    void testSaveUser() {
        testUser.setPassword("rawPassword");

        userService.saveUser(testUser);

        verify(userRepository, times(1)).save(testUser);
        assertNotEquals("rawPassword", testUser.getPassword());  // Password should be encoded
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(testUser.getId());

        verify(userRepository, times(1)).deleteById(testUser.getId());
    }

    @Test
    void testUpdateBalance_AccountNotFound() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountServiceClient.getAccountsOfUser(testUser.getId())).thenReturn(List.of(new Account(2L,"ABC", 100.0, 1L)));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateBalance(testUser.getId(), 100.0, 1L);
        });

        assertEquals("Account Not Found", exception.getMessage());
    }

    @Test
    void testBuyStock_BalanceSufficient() {
        Stock stock = new Stock();
        stock.setPrice(50.0);
        Account account = new Account(1L,"ABC", 100.0, 1L);

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
        verify(accountServiceClient, times(1)).updateAccountInAccountService(account);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testSellStock() {
        Stock stock = new Stock();
        stock.setPrice(50.0);
        Account account =new Account(1L,"ABC", 100.0, 1L);

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
        verify(accountServiceClient, times(1)).updateAccountInAccountService(account);
        verify(userRepository, times(1)).save(testUser);
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
