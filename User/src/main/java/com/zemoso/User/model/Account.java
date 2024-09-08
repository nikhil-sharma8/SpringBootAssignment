package com.zemoso.User.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;

    private String accountNumber;

    private Double amount;

    private Long userId;
}
