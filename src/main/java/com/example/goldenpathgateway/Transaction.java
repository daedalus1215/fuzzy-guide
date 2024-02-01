package com.example.goldenpathgateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Serializable {
    private long transactionId;

    private long date;
    private double amount;
    private String merchantName;
    private String summary;
    private Account account;
}
