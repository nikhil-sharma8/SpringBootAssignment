package com.zemoso.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String occupation;

    private String email;

    private String password;

    @Transient
    private List<Account> accounts = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_stocks", joinColumns = @JoinColumn(name = "user_id"))
    private List<Stock> userStocks = new ArrayList<>();

    public User(long l, String nikhil, String devIntern, String mail, String number) {
        this.id = l;
        this.name = nikhil;
        this.occupation = devIntern;
        this.email = mail;
        this.password = number;
    }

    public void sellStockBySymbol(String stockSymbol) {
        if (this.userStocks != null) {
            this.userStocks.removeIf(stock -> stock.getSymbol().equalsIgnoreCase(stockSymbol));
        }
    }
}
