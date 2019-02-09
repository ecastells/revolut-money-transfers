package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.AccountDTO;
import com.revolut.moneytransfers.model.Account;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AccountServiceImpl implements AccountService {

    AccountDTO accountDTO;

    @Inject
    public AccountServiceImpl(AccountDTO accountDTO) {
        this.accountDTO = accountDTO;
    }

    List<Account> accounts = new ArrayList<>();
    long id = 0;

    @Override
    public Account getAccountById(Long id) {
        return accountDTO.getAccount(id);
       /* for (Account account: accounts){
            if (id.equals(account.getId())){
                return account;
            }
        }
        return null;*/
    }

    @Override
    public List<Account> getAccounts() {
        return accountDTO.getAccounts();
      //  return accounts;
    }

    @Override
    public Account createAccount(Account account) {
        account.setId(++id);
        accounts.add(account);
        return account;
    }
}
