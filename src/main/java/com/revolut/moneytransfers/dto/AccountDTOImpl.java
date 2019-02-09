package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AccountDTOImpl implements AccountDTO {

    private DBUtil dbUtil;
    private static final String GET_ACCOUNTS = "SELECT * FROM account";
    private static final String GET_ACCOUNTS_BY_ID = "SELECT * FROM account where id = ?";

    @Inject
    public AccountDTOImpl(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public List<Account> getAccounts(){
        return dbUtil.executeOnlyReadQuery(GET_ACCOUNTS, preparedStatement -> {
            List<Account> accounts = new ArrayList<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs != null){
                    while (rs.next()) {
                        accounts.add(fromResultSet(rs));
                    }
                }
            }
            return accounts;
        }).getResult();
    }

    public Account getAccount(Long id){
        return dbUtil.executeOnlyReadQuery(GET_ACCOUNTS, preparedStatement -> {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs != null){
                    if (rs.next()) {
                        Account account = fromResultSet(rs);
                        return account;
                    }
                }
            }
            return null;
        }).getResult();
    }


   private Account fromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setOwner(rs.getString("owner"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setPendingTransfer(rs.getBigDecimal("pendingTransfer"));
        return account;
    }
}
