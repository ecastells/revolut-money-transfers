package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.AccountDTO;
import com.revolut.moneytransfers.error.ValidationException;
import com.revolut.moneytransfers.model.Account;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
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
        // Verify that there are not null for required input parameters
        if (account.getOwner() == null || account.getOwner().trim().isEmpty()
                || account.getCurrency() == null || account.getBalance() == null) {
            throw new ValidationException("owner, currency and balance must not be null or empty");
        }

        // Verify that the balance is greater than zero
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 1){
            throw new ValidationException("balance must be greater than 0");
        }

        return accountDTO.createAccount(account);
    }
}
