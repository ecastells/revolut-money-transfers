package com.revolut.moneytransfers.config;

import com.google.inject.AbstractModule;
import com.revolut.moneytransfers.db.DBConnection;
import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.db.DBUtilImpl;
import com.revolut.moneytransfers.db.H2Connection;
import com.revolut.moneytransfers.dto.AccountDTO;
import com.revolut.moneytransfers.dto.AccountDTOImpl;
import com.revolut.moneytransfers.dto.TransactionDTO;
import com.revolut.moneytransfers.dto.TransactionDTOImpl;
import com.revolut.moneytransfers.service.AccountService;
import com.revolut.moneytransfers.service.AccountServiceImpl;
import com.revolut.moneytransfers.service.TransactionService;
import com.revolut.moneytransfers.service.TransactionServiceImpl;

/**
 * The inject configuration class. Used to inject all bean dependencies at runtime
 */
public class InjectionConfiguration extends AbstractModule {

    @Override
    protected void configure() {
        // Ingest general configuration
        Configuration configuration = new Configuration();
        bind(Configuration.class).toInstance(configuration);

        // Ingest DB connection
        bind(DBConnection.class).to(H2Connection.class);
        bind(DBUtil.class).to(DBUtilImpl.class);

        // Ingest Endpoints
        // Account
        bind(AccountDTO.class).to(AccountDTOImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
        // Transaction
        bind(TransactionDTO.class).to(TransactionDTOImpl.class);
        bind(TransactionService.class).to(TransactionServiceImpl.class);
    }
}
