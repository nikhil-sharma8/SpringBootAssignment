package com.zemoso.user.service;

import com.zemoso.user.config.AccountServiceClient;
import com.zemoso.user.config.JWTService;
import com.zemoso.user.config.StockServiceClient;
import com.zemoso.user.dto.UserToken;
import com.zemoso.user.model.Account;
import com.zemoso.user.model.MyUserDetails;
import com.zemoso.user.model.Stock;
import com.zemoso.user.model.User;
import com.zemoso.user.repository.IUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService, IStockService {

    IUserRepository userRepository;

    AccountServiceClient accountServiceClient;

    StockServiceClient stockServiceClient;

    AuthenticationManager authenticationManager;

    JWTService service;

    UserToken token;

    String accountNotFound = "Account Not Found";

    UserServiceImpl(IUserRepository userRepository, AccountServiceClient accountServiceClient, StockServiceClient stockServiceClient, AuthenticationManager authenticationManager, JWTService service, UserToken userToken) {
        this.userRepository = userRepository;
        this.accountServiceClient = accountServiceClient;
        this.stockServiceClient = stockServiceClient;
        this.authenticationManager = authenticationManager;
        this.service = service;
        this.token = userToken;
    }

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    user.setAccounts(accountServiceClient.getAccountsOfUser(user.getId()));
                    return user;
                })
                .toList();
    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccounts(accountServiceClient.getAccountsOfUser(user.getId()));
        user.setUserStocks(user.getUserStocks());
        return user;
    }

    public String saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User saved";
    }

    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return "User deleted";
    }

    @Override
    public UserToken verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));

        token.setUsername(user.getName());
        token.setIsAuthenticated(authentication.isAuthenticated());

        if (Boolean.TRUE.equals(token.getIsAuthenticated())) {
            token.setToken(service.generateToken(user.getName()));
            token.setExpiration("30 minutes");
        }

        return token;
    }

    @Override
    public String updateBalance(Long userId, Double amount, Long accountId) {
        User user = getUserById(userId);

        Account userAccount = user.getAccounts().stream().filter(account -> account.getId().equals(accountId)).findFirst().orElseThrow(() -> new RuntimeException(accountNotFound));

        userAccount.setAmount(userAccount.getAmount() + amount);
        accountServiceClient.updateAccountInAccountService(userAccount);

        userRepository.save(user);
        return "Account Updated";
    }

    @Override
    public List<Stock> getOwnedStocks() {
        User user = userRepository.findByName(getUserData());
        return user.getUserStocks();
    }

    @Override
    public List<Stock> getFullStockList() {
        return stockServiceClient.getAllStocks();
    }

    @Override
    public String buyStock(String stockSymbol, Long accountId) {
        Stock stock = stockServiceClient.getStockBySymbol(stockSymbol);

        User user = userRepository.findByName(getUserData());
        user.setAccounts(accountServiceClient.getAccountsOfUser(user.getId()));
        Account userAccount = user.getAccounts().stream().filter(account -> account.getId().equals(accountId)).findFirst().orElseThrow(() -> new RuntimeException(accountNotFound));

        if (userAccount.getAmount() < stock.getPrice()) {
            return "Account balance not sufficient";
        }

        userAccount.setAmount(userAccount.getAmount() - stock.getPrice());

        accountServiceClient.updateAccountInAccountService(userAccount);

        List<Stock> userStock = user.getUserStocks();
        userStock.add(stock);
        user.setUserStocks(userStock);
        userRepository.save(user);
        return "Stock Bought";
    }

    @Override
    public String sellStock(String stockSymbol, Long accountId) {
        User user = userRepository.findByName(getUserData());
        user.setAccounts(accountServiceClient.getAccountsOfUser(user.getId()));
        Account userAccount = user.getAccounts().stream().filter(account -> account.getId().equals(accountId)).findFirst().orElseThrow(() -> new RuntimeException(accountNotFound));

        Stock stock = stockServiceClient.getStockBySymbol(stockSymbol);
        userAccount.setAmount(userAccount.getAmount() + stock.getPrice());

        accountServiceClient.updateAccountInAccountService(userAccount);
        user.sellStockBySymbol(stockSymbol);
        userRepository.save(user);
        return "Stock Sold";
    }


    public String getUserData() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }

}
