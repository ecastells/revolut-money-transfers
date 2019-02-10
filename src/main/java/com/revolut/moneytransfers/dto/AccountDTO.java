package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public interface AccountDTO {
    List<Account> getAccounts();
    Account getAccount(Long id);
    Account createAccount(Account account);
    Account getAccountToBeUpdate(Connection con, Long id);
    Account updateAccountBalance(Connection con, Long accountId, BigDecimal newBalance, BigDecimal pendingTransfer);
}
