package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.model.Account;

import java.util.List;

public interface AccountService {

    /**
     * Returns a {@link Account} object by its id specified
     *
     * @param id the id of the Account
     * @return Account object with id specified
     */
    Account getAccountById(Long id);
    /**
     * Returns the {@link List<Account>}
     * @return the list of Accounts
     *
     */
    List<Account> getAccounts();

    /**
     * Creates an {@link Account} and validate the incoming account parameters
     *
     * @param account the account to create
     * @return Account created with its id
     */
    Account createAccount(Account account);
}
