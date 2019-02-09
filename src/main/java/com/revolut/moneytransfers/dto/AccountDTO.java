package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.model.Account;

import java.util.List;

public interface AccountDTO {
    List<Account> getAccounts();
    Account getAccount(Long id);
}
