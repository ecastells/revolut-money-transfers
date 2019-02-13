package com.revolut.moneytransfers.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction implements Entity{
    private Long id;
    private Long fromAccountId;
    private BigDecimal amount;
    private Currency currency;
    private Long toAccountId;
    private TransactionStatus status;
    private Timestamp creationDate;
    private Timestamp lastUpdatedDate;
    private Integer retryCreation;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getRetryCreation() {
        return retryCreation;
    }

    public void setRetryCreation(Integer retryCreation) {
        this.retryCreation = retryCreation;
    }

    public enum TransactionStatus {
        PENDING, CONFIRMED, REJECTED
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", fromAccountId=" + fromAccountId +
                ", amount=" + amount +
                ", currency=" + currency +
                ", toAccountId=" + toAccountId +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", retryCreation=" + retryCreation +
                '}';
    }
}
