package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.model.Account;

import java.util.List;

public interface AccountService {
    Account getAccountById(Long id);
    List<Account> getAccounts();
    Account createAccount(Account account);
}
