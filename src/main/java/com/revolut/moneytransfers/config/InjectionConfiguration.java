package com.revolut.moneytransfers.config;

import com.google.inject.AbstractModule;
import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.service.AccountService;
import com.revolut.moneytransfers.service.AccountServiceImpl;
import spark.Spark;

public class InjectionConfiguration extends AbstractModule {

    @Override
    protected void configure() {
        Spark.port(8080);
        Spark.staticFileLocation("/moneytransfers");

        AccountService accountService = new AccountServiceImpl();
        AccountController accountController = new AccountController(accountService);
        bind(AccountService.class).toInstance(accountService);
        bind(AccountController.class).toInstance(accountController);
    }
}
