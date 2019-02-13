package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.model.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction getTransactionById(Long id);
    List<Transaction> getTransactions(Transaction.TransactionStatus status);
    Transaction createTransaction(Transaction transaction);
    void processTransactions();
}
