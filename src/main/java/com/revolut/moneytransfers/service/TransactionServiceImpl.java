package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.TransactionDTO;
import com.revolut.moneytransfers.error.ValidationException;
import com.revolut.moneytransfers.model.Transaction;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
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
    public List<Transaction> getTransactions(Transaction.TransactionStatus status) {
        return transactionDTO.getTransactions(status);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        // Verify that there are not null for required input parameters
        if (transaction.getFromAccountId() == null || transaction.getToAccountId() == null
            || transaction.getCurrency() == null || transaction.getAmount() == null) {
            throw new ValidationException("fromAccountId, amount, currency, toAccountId must not be null");
        }

        // Verify that FromAccountId is not equal to ToAccountId
        if (transaction.getFromAccountId().equals(transaction.getToAccountId())){
            throw new ValidationException("fromAccountId and toAccountId must not be equals");
        }

        // Verify that the id and money to transfer is greater than zero
        if (transaction.getFromAccountId() < 1 || transaction.getToAccountId() < 1 || transaction.getAmount().compareTo(BigDecimal.ZERO) < 1){
            throw new ValidationException("fromAccountId, amount, toAccountId must be greater than 0");
        }

        return transactionDTO.createTransaction(transaction);
    }
}
