package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.model.Transaction;

import java.util.List;

public interface TransactionService {

    /**
     * Returns a {@link Transaction} object by its id specified
     *
     * @param id the id of the Transaction
     * @return Transaction object with id specified
     */
    Transaction getTransactionById(Long id);

    /**
     * Returns the {@link List<Transaction>}
     *
     * @param status the status of the transaction, if it is null all transactions should be returned
     * @return the list of Transactions
     *
     */
    List<Transaction> getTransactions(Transaction.TransactionStatus status);

    /**
     * Creates a {@link Transaction} and validate the incoming account parameters.
     *
     * @param transaction the transaction to be created
     * @return the list of Transactions
     *
     */
    Transaction createTransaction(Transaction transaction);

    /**
     * Process a list of {@link Transaction} where its status is PENDING in order to mark them as CONFIRMED if they
     * are fine or REJECTED if there is a problem.
     * When it is mark as CONFIRMED the amount specified in the transaction is sent to the destination account.
     * When it is mark as REJECTED the amount specified in the transaction is return back to the origin account.
     *
     *
     */
    void processTransactions();
}
