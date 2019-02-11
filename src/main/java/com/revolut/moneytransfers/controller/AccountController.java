package com.revolut.moneytransfers.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfers.config.Configuration;
import com.revolut.moneytransfers.error.ResponseError;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.service.AccountService;
import spark.Spark;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TransactionController class. Provide operations over RestFul to Retrieve and Create Transactions:
 * - POST /account: Allow to create a Account with a initial balance and kind of currency
 * - GET /account: Allow to retrieve All Accounts created on the System
 * - GET /account/:id: Allow to retrieve a specific Account according to its id
 */
@Singleton
public class AccountController extends GenericController {

    @Inject
    public AccountController(Configuration configuration, AccountService accountService) {
        super();

        Spark.post(configuration.getAccountPath(), (request, response) -> {
            Account account = new Gson().fromJson(request.body(), Account.class);
            Account accountCreated = accountService.createAccount(account);
            if (accountCreated != null){
                response.status(201);
                return accountCreated;
            } else {
                response.status(405);
                return new ResponseError("Error creating Account");
            }
        }, json());

        Spark.get(configuration.getAccountPath(), (request, response) -> accountService.getAccounts(), json());

        Spark.get(configuration.getAccountPath() + "/:id", (request, response) ->
        {
            Account accountById = accountService.getAccountById(Long.parseLong(request.params(":id")));
            if (accountById != null){
                return accountById;
            } else {
                response.status(404);
                return new ResponseError("Account not found");
            }
        }, json());
    }
}
