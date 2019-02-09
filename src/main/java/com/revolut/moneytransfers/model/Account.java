package com.revolut.moneytransfers.model;

import java.math.BigDecimal;

public class Account implements Entity{
    Long id;
    private String owner;
    private BigDecimal balance;
    private BigDecimal pendingTransfer;
    private Currency currency;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPendingTransfer() {
        return pendingTransfer;
    }

    public void setPendingTransfer(BigDecimal pendingTransfer) {
        this.pendingTransfer = pendingTransfer;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", balance=" + balance +
                ", pendingTransfer=" + pendingTransfer +
                ", currency=" + currency +
                '}';
    }
}
