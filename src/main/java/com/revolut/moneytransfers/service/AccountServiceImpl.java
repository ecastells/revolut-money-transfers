package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.model.Account;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AccountServiceImpl implements AccountService {

    List<Account> accounts = new ArrayList<>();
    long id = 0;

    @Override
    public Account getAccountById(Long id) {
        for (Account account: accounts){
            if (id.equals(account.getId())){
                return account;
            }
        }
        return null;
    }

    @Override
    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public Account createAccount(Account account) {
        account.setId(++id);
        accounts.add(account);
        return account;
    }
}
