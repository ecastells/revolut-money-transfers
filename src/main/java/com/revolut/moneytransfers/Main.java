package com.revolut.moneytransfers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.moneytransfers.config.InjectionConfiguration;
import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.controller.TransactionController;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new InjectionConfiguration());
        injector.getInstance(TransactionController.class);
        injector.getInstance(AccountController.class);
    }
}
