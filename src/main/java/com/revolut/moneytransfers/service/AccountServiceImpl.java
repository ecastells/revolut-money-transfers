package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Singleton
public class AccountServiceImpl implements AccountService {

    @Override
    public Account getAccountById(Long id) {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.ONE);
        account.setCurrency(new Currency());
        account.setOwner("Emi");
        return account;
    }

    @Override
    public List<Account> getAccounts() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.ONE);
        account.setCurrency(new Currency());
        account.setOwner("Emiliano");

        Account account2 = new Account();
        account2.setId(2L);
        account2.setBalance(BigDecimal.TEN);
        Currency c = new Currency();
        c.setId(1);
        c.setType("USD");
        account2.setCurrency(c);
        account2.setOwner("Grecia");
        return Arrays.asList(account, account2);
    }
}
