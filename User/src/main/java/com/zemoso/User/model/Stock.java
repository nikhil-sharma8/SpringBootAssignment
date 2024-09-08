package com.zemoso.User.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {

    private Long id;
    private String symbol;
    private String name;
    private Double price;
    private long volume;
    private LocalDate date;
}
