package com.revolut.moneytransfers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.moneytransfers.config.InjectionConfiguration;
import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.controller.TransactionController;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new InjectionConfiguration());
        TransactionController transactionController = injector.getInstance(TransactionController.class);
        injector.getInstance(AccountController.class);

        // Execute a process every 10 seconds to confirm the transaction
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(transactionController::processTransaction,
                5, 5, TimeUnit.SECONDS);
    }
}
