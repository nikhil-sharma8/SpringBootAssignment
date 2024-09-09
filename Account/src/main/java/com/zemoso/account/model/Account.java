package com.zemoso.account.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;

    private Double amount;

    private Long userId;

    public Account(long l, String account1, double v) {
        this.id=l;
        this.accountNumber = account1;
        this.amount = v;
    }
    Random random = new Random();
    @PrePersist
    public void generateAccountNumber(){
        this.accountNumber = "ACC-" +random.nextInt(10000);
    }
}
