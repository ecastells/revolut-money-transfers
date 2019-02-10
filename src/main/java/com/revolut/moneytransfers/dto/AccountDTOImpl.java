package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AccountDTOImpl implements AccountDTO {

    private DBUtil dbUtil;
    public static final String GET_ACCOUNTS = "SELECT * FROM account";
    public static final String GET_ACCOUNT_BY_ID = "SELECT * FROM account where id = ?";
    public static final String INSERT_ACCOUNT = "INSERT INTO account (owner, balance, pending_transfer, currency_id) VALUES (?, ?, ?, ?)";

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
        return dbUtil.executeOnlyReadQuery(GET_ACCOUNT_BY_ID, preparedStatement -> {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs != null && rs.next()){
                    return fromResultSet(rs);
                }
            }
            return null;
        }).getResult();
    }

    @Override
    public Account createAccount(Account account) {
        return dbUtil.executeQuery(INSERT_ACCOUNT, preparedStatement -> {
            preparedStatement.setString(1, account.getOwner());
            preparedStatement.setBigDecimal(2, account.getBalance());
            preparedStatement.setBigDecimal(3, account.getPendingTransfer());
            preparedStatement.setLong(4, account.getCurrency().getId());
            int rows = preparedStatement.executeUpdate();
            Long generatedId = null;

            if (rows != 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getLong(1);
                    }
                }
            }

            if (generatedId == null) {
                return null;
            }
            account.setId(generatedId);
            return account;
        }).getResult();
    }


    private Account fromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setOwner(rs.getString("owner"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setPendingTransfer(rs.getBigDecimal("pending_transfer"));
        account.setCurrency(Currency.getCurrencyById(rs.getLong("currency_id")));
        return account;
    }
}
