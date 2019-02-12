package com.revolut.moneytransfers.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfers.config.Config;
import com.revolut.moneytransfers.error.ResponseError;
import com.revolut.moneytransfers.model.Transaction;
import com.revolut.moneytransfers.service.TransactionService;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TransactionController class. Provide operations over RestFul to Retrieve and Create Transactions:
 * - POST /transaction: Allow to create a Transaction for money transfers between accounts. The transaction state is PENDING until it is confirmed
 * - GET /transaction?status=status: Allow to retrieve All Transactions created on the System filter by status. If the parameter is not present the filter is not used
 * - GET /transaction/:id: Allow to retrieve a specific Transaction according to its id
 */
@Singleton
public class TransactionController extends GenericController {

    @Inject
    public TransactionController(Config configuration, TransactionService transactionService) {
        super();

        configuration.getService().post(configuration.getTransactionPath(), (request, response) -> {
            Transaction transaction = new Gson().fromJson(request.body(), Transaction.class);
            Transaction transactionCreated = transactionService.createTransaction(transaction);
            if (transactionCreated != null){
                response.status(201);
                return transactionCreated;
            } else {
                response.status(405);
                return new ResponseError("Error creating Transaction", ResponseError.ErrorCode.C500);
            }
        }, json());

        configuration.getService().get(configuration.getTransactionPath(), (request, response) -> {
            String statusParam = request.queryParams("status");
            Transaction.TransactionStatus status = null;
            if (statusParam != null && !statusParam.isEmpty()){
                status = Transaction.TransactionStatus.valueOf(statusParam.toUpperCase());
            }
            response.status(200);
            return transactionService.getTransactions(status);
        }, json());

        configuration.getService().get(configuration.getTransactionPath() + "/:id", (request, response) ->
        {
            Transaction transactionById = transactionService.getTransactionById(Long.parseLong(request.params(":id")));
            if (transactionById != null){
                response.status(200);
                return transactionById;
            } else {
                response.status(404);
                return new ResponseError("Transaction not found", ResponseError.ErrorCode.C500);
            }
        }, json());
    }
}
