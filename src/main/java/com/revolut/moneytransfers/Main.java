package com.revolut.moneytransfers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.moneytransfers.config.InjectionConfiguration;
import com.revolut.moneytransfers.controller.AccountController;

public class Main {
    public static void main(String[] args) {


        Injector injector = Guice.createInjector(new InjectionConfiguration());
        AccountController accountController = injector.getInstance(AccountController.class);

    /*    Spark.get("/hello", (req, res) -> "Hello, Baeldung");

        Spark.get("/hello/:name", (req, res) -> {
            return "Hello: " + req.params(":name");
        });*/
    }
}
