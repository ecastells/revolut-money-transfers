package com.revolut.moneytransfers.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfers.config.Configuration;
import com.revolut.moneytransfers.error.ResponseError;
import com.revolut.moneytransfers.model.Transaction;
import com.revolut.moneytransfers.service.TransactionService;
import spark.Spark;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TransactionController class. Provide operations over RestFul to Retrieve and Create Transactions:
 * - POST /transaction: Allow to create a Transaction for money transfers between accounts. The transaction state is PENDING until it is confirmed
 * - GET /transaction: Allow to retrieve All Transactions created on the System
 * - GET /transaction/:id: Allow to retrieve a specific Transaction according to its id
 */
@Singleton
public class TransactionController extends GenericController {

    @Inject
    public TransactionController(Configuration configuration, TransactionService transactionService) {
        super();

        Spark.post(configuration.getTransactionPath(), (request, response) -> {
            Transaction transaction = new Gson().fromJson(request.body(), Transaction.class);
            Transaction transactionCreated = transactionService.createTransaction(transaction);
            if (transactionCreated != null){
                response.status(201);
                return transactionCreated;
            } else {
                response.status(405);
                return new ResponseError("Error creating Transaction");
            }
        }, json());

        Spark.get(configuration.getTransactionPath(), (request, response) -> transactionService.getTransactions(), json());

        Spark.get(configuration.getTransactionPath() + "/:id", (request, response) ->
        {
            Transaction transactionById = transactionService.getTransactionById(Long.parseLong(request.params(":id")));
            if (transactionById != null){
                return transactionById;
            } else {
                response.status(404);
                return new ResponseError("Transaction not found");
            }
        }, json());
    }
}
