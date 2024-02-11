package com.example.goldenpathgateway;

import java.io.Serializable;


public class TransactionDto implements Serializable {
    private String transactionId;
    private String date;
    private double amount;
    private String merchantName;
    private String summary;
    private String accountId;

    public TransactionDto() {
    }

    public TransactionDto(String transactionId, String date, double amount, String merchantName, String summary, String accountId) {
        this.transactionId = transactionId;
        this.date = date;
        this.amount = amount;
        this.merchantName = merchantName;
        this.summary = summary;
        this.accountId = accountId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
