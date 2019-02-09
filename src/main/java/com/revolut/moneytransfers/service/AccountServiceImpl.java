package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.AccountDTO;
import com.revolut.moneytransfers.model.Account;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AccountServiceImpl implements AccountService {

    AccountDTO accountDTO;

    @Inject
    public AccountServiceImpl(AccountDTO accountDTO) {
        this.accountDTO = accountDTO;
    }

    @Override
    public Account getAccountById(Long id) {
        return accountDTO.getAccount(id);
    }

    @Override
    public List<Account> getAccounts() {
        return accountDTO.getAccounts();
    }

    @Override
    public Account createAccount(Account account) {
        return accountDTO.createAccount(account);
    }
}
