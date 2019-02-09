package com.revolut.moneytransfers.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfers.error.ResponseError;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.service.AccountService;
import spark.ResponseTransformer;
import spark.Spark;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountController {

    AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;

        Spark.get("/account", (req, res) -> accountService.getAccounts(), json());

        Spark.get("/account/:id", (req, res) -> accountService.getAccountById(Long.parseLong(req.params(":id"))), json());

        Spark.after((req, res) -> {
            res.type("application/json");
        });

        Spark.exception(IllegalArgumentException.class, (error, req, res) -> {
            res.status(500);
            res.body(toJson(new ResponseError(error)));
        });
    }

    private static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    private ResponseTransformer json() {
        return AccountController::toJson;
    }
}
