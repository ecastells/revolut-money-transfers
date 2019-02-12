package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Singleton
public class AccountDTOImpl implements AccountDTO, GenericDTO<Account>{

    private DBUtil dbUtil;
    public static final String GET_ACCOUNTS = "SELECT * FROM account";
    public static final String GET_ACCOUNT_BY_ID = GET_ACCOUNTS + " where id = ?";
    public static final String GET_ACCOUNT_BY_ID_TO_BE_UPDATED = GET_ACCOUNT_BY_ID + " FOR UPDATE";
    public static final String INSERT_ACCOUNT = "INSERT INTO account (owner, balance, pending_transfer, currency_id) VALUES (?, ?, ?, ?)";
    public static final String UPDATE_ACCOUNT_BALANCE_PENDING_TRANSFER = "UPDATE account SET balance = ?, pending_transfer = ? where id = ?";
    public static final String UPDATE_ACCOUNT_BALANCE = "UPDATE account SET balance = ? where id = ?";

    @Inject
    public AccountDTOImpl(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public List<Account> getAccounts(){
        return dbUtil.executeQuery(true, GET_ACCOUNTS, this::getListEntities).getResult();
    }

    public Account getAccount(Long id){
        return dbUtil.executeQuery(true, GET_ACCOUNT_BY_ID, preparedStatement -> {
            preparedStatement.setLong(1, id);
            return getEntity(preparedStatement);
        }).getResult();
    }

    @Override
    public Account createAccount(Account account) {
        return dbUtil.executeQuery(false, INSERT_ACCOUNT, preparedStatement -> {
            preparedStatement.setString(1, account.getOwner());
            preparedStatement.setBigDecimal(2, account.getBalance());
            preparedStatement.setBigDecimal(3, BigDecimal.ZERO);
            preparedStatement.setLong(4, account.getCurrency().getId());
            return !insertEntity(account, preparedStatement) ? null : account;
        }).getResult();
    }

    @Override
    public Account getAccountToBeUpdate(Connection con, Long id) {
        return dbUtil.executeQueryInTransaction(con, GET_ACCOUNT_BY_ID_TO_BE_UPDATED, preparedStatement -> {
            preparedStatement.setLong(1, id);
            return getEntity(preparedStatement);
        }).getResult();
    }

    @Override
    public Account updateAccountBalance(Connection con, Long accountId, BigDecimal newBalance, BigDecimal pendingTransfer) {
        return dbUtil.executeQueryInTransaction(con, pendingTransfer != null ? UPDATE_ACCOUNT_BALANCE_PENDING_TRANSFER : UPDATE_ACCOUNT_BALANCE, preparedStatement -> {
            preparedStatement.setBigDecimal(1, newBalance);
            if (pendingTransfer != null){
                preparedStatement.setBigDecimal(2, pendingTransfer);
                preparedStatement.setLong(3, accountId);
            } else {
                preparedStatement.setLong(2, accountId);
            }
            Account account = new Account();
            account.setId(accountId);
            account.setBalance(newBalance);
            account.setPendingTransfer(pendingTransfer);
            return !updateEntity(account, preparedStatement) ? null : account;
        }).getResult();
    }

    @Override
    public Account fromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong("id"));
        account.setOwner(rs.getString("owner"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setPendingTransfer(rs.getBigDecimal("pending_transfer"));
        account.setCurrency(Currency.getCurrencyById(rs.getLong("currency_id")));
        return account;
    }
}
