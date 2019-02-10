package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.model.Transaction;

import java.util.List;

public interface TransactionDTO {
    Transaction getTransactionById(Long id);
    List<Transaction> getTransactions();
    Transaction createTransaction(Transaction transaction);
}
