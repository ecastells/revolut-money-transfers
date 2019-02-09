package com.revolut.moneytransfers.config;

import com.google.inject.AbstractModule;
import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.db.DBConnection;
import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.db.DBUtilImpl;
import com.revolut.moneytransfers.db.H2Connection;
import com.revolut.moneytransfers.dto.AccountDTO;
import com.revolut.moneytransfers.dto.AccountDTOImpl;
import com.revolut.moneytransfers.service.AccountService;
import com.revolut.moneytransfers.service.AccountServiceImpl;

public class InjectionConfiguration extends AbstractModule {

    @Override
    protected void configure() {
       Configuration configuration = new Configuration();
        bind(Configuration.class).toInstance(configuration);

        DBConnection dbConnection = new H2Connection(configuration);
        bind(DBConnection.class).toInstance(dbConnection);

        DBUtil dbUtil = new DBUtilImpl(dbConnection);
        bind(DBUtil.class).toInstance(dbUtil);

        AccountDTOImpl accountDTO = new AccountDTOImpl(dbUtil);
        bind(AccountDTO.class).toInstance(accountDTO);

        AccountService accountService = new AccountServiceImpl(accountDTO);
        AccountController accountController = new AccountController(configuration, accountService);
        bind(AccountService.class).toInstance(accountService);
        bind(AccountController.class).toInstance(accountController);


    }
}
