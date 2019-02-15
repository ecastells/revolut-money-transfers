package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.model.Transaction;

import java.util.List;

public interface TransactionDTO {

    /**
     * Returns a {@link Transaction} object from the database by its id specified
     *
     * @param id the id of the Transaction
     * @return Transaction object with id specified
     */
    Transaction getTransactionById(Long id);

    /**
     * Returns the {@link List<Transaction>} from the database
     * @param status the status of the transaction, if it is null all transactions should be returned
     * @return the list of Transactions
     *
     */
    List<Transaction> getTransactions(Transaction.TransactionStatus status);

    /**
     * Creates a {@link Transaction} into the database.
     * The new transaction status is PENDING and its retryCreation is 0.
     * This should check that the origin account has enough money before creating the transaction
     *
     * @param transaction the transaction to be created
     * @return the list of Transactions
     *
     */
    Transaction createTransaction(Transaction transaction);

    /**
     * Process a {@link Transaction}. If the transaction is fine. The balance of the destination account
     * should be increased by the amount specified and the transaction mark as CONFIRMED
     * Otherwise, it should try a number of configured retries, and if it is reached return back the money
     * to the origin account and mark the transaction as REJECTED
     *
     * @param id the of the transaction to be processed
     *
     */
    void processTransaction(Long id);
}
