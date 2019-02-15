package com.revolut.moneytransfers.config;

import com.google.inject.AbstractModule;
import com.revolut.moneytransfers.db.DBConnection;
import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.db.DBUtilImpl;
import com.revolut.moneytransfers.db.H2Connection;
import com.revolut.moneytransfers.dto.*;
import com.revolut.moneytransfers.service.AccountService;
import com.revolut.moneytransfers.service.AccountServiceImpl;
import com.revolut.moneytransfers.service.TransactionService;
import com.revolut.moneytransfers.service.TransactionServiceImpl;

/**
 * The inject configuration class. Used to inject all bean dependencies at runtime in Test envirotment
 */
public class IngestConfigurationTest extends AbstractModule {

    @Override
    protected void configure() {
        // Ingest general configuration
        ConfigurationTest configurationTest = new ConfigurationTest(8081, 5, "MoneyTransferTest");
        bind(Config.class).toInstance(configurationTest);

        // Ingest DB connection
        bind(DBConnection.class).to(H2Connection.class);
        bind(DBUtil.class).to(DBUtilImpl.class);

        // Ingest Services
        // Account
        bind(AccountDTO.class).to(AccountDTOImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
        // Transaction
        bind(TransactionDTO.class).to(TransactionDTOImpl.class);
        bind(TransactionService.class).to(TransactionServiceImpl.class);
        // CurrencyConversion
        bind(CurrencyConversionDTO.class).to(CurrencyConversionDTOImpl.class);
    }
}
