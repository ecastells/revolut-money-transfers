package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public interface AccountDTO {

    /**
     * Returns the {@link List<Account>} from the database
     * @return the list of Accounts
     *
     */
    List<Account> getAccounts();

    /**
     * Returns a {@link Account} object from the database by its id specified
     *
     * @param id the id of the Account
     * @return Account object with id specified
     */
    Account getAccount(Long id);

    /**
     * Creates an {@link Account}
     *
     * @param account the account to create on the database
     * @return Account created with its id
     */
    Account createAccount(Account account);

    /**
     * Returns a {@link Account} object from the database by its id specified
     * This method should receive the connection in order to not be closed once result
     *
     * @param con the connection used for this query
     * @param id the id of the Account
     * @return Account object with id specified
     */
    Account getAccountToBeUpdate(Connection con, Long id);

    /**
     * Returns the updated {@link Account} object from the database
     * This method should receive the connection in order to not be closed once result
     *
     * @param con the connection used for this query
     * @param accountId the id of the Account
     * @param newBalance the new balance of the account to be updated
     * @return Account updated
     */
    Account updateAccountBalance(Connection con, Long accountId, BigDecimal newBalance);
}
