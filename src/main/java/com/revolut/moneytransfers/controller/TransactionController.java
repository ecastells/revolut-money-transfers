package com.revolut.moneytransfers.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfers.config.Configuration;
import com.revolut.moneytransfers.model.Transaction;
import com.revolut.moneytransfers.service.TransactionService;
import spark.Spark;

import javax.inject.Inject;
import javax.inject.Singleton;

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
                return "Error creating Transaction";
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
                return "Transaction not found";
            }
        }, json());
    }
}
