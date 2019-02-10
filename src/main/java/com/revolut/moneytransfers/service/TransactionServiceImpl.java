package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.TransactionDTO;
import com.revolut.moneytransfers.model.Transaction;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TransactionServiceImpl implements TransactionService {

    TransactionDTO transactionDTO;

    @Inject
    public TransactionServiceImpl(TransactionDTO transactionDTO) {
        this.transactionDTO = transactionDTO;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionDTO.getTransactionById(id);
    }

    @Override
    public List<Transaction> getTransactions() {
        return transactionDTO.getTransactions();
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionDTO.createTransaction(transaction);
    }
}
