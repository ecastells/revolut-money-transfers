package com.revolut.moneytransfers.config;

import com.google.inject.AbstractModule;
import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.controller.AccountControllerImpl;
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
        bind(DBConnection.class).to(H2Connection.class);
        bind(DBUtil.class).to(DBUtilImpl.class);
        bind(AccountDTO.class).to(AccountDTOImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(AccountController.class).to(AccountControllerImpl.class);
    }
}
