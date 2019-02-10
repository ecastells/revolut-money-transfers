package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.Transaction;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Singleton
public class TransactionDTOImpl implements TransactionDTO, GenericDTO<Transaction> {

    private DBUtil dbUtil;
    public static final String GET_TRANSACTIONS = "SELECT * FROM transaction";
    public static final String GET_TRANSACTION_BY_ID = "SELECT * FROM transaction where id = ?";
    public static final String INSERT_TRANSACTION = "INSERT INTO transaction " +
            "(from_account_id, amount, currency_id, to_account_id, status, creation_date) VALUES (?, ?, ?, ?, ?, ?)";

    @Inject
    public TransactionDTOImpl(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return dbUtil.executeOnlyReadQuery(GET_TRANSACTION_BY_ID, preparedStatement -> {
            preparedStatement.setLong(1, id);
            return getEntity(preparedStatement);
        }).getResult();
    }

    @Override
    public List<Transaction> getTransactions() {
        return dbUtil.executeOnlyReadQuery(GET_TRANSACTIONS, preparedStatement -> getListEntities(preparedStatement)).getResult();
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return dbUtil.executeQuery(INSERT_TRANSACTION, preparedStatement -> {
            preparedStatement.setLong(1, transaction.getFromAccountId());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setLong(3, transaction.getCurrency().getId());
            preparedStatement.setLong(4, transaction.getToAccountId());
            preparedStatement.setString(5, Transaction.TransactionStatus.PENDING.name());
            preparedStatement.setTimestamp(6, Timestamp.from(Instant.now()));

            return !insertEntity(transaction, preparedStatement) ? null : transaction;
        }).getResult();
    }

    @Override
    public Transaction fromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setFromAccountId(rs.getLong("from_account_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setCurrency(Currency.getCurrencyById(rs.getLong("currency_id")));
        transaction.setToAccountId(rs.getLong("to_account_id"));
        transaction.setStatus(rs.getString("status"));
        transaction.setCreationDate(rs.getTimestamp("creation_date"));
        transaction.setLastUpdatedDate(rs.getTimestamp("last_update_date"));
        return transaction;
    }
}
