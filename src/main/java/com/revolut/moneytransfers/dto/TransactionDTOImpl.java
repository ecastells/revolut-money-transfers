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
import java.util.ArrayList;
import java.util.List;

@Singleton
public class TransactionDTOImpl implements TransactionDTO {

    private DBUtil dbUtil;
    public static final String GET_TRANSACTIONS = "SELECT * FROM transaction";
    public static final String GET_TRANSACTION_BY_ID = "SELECT * FROM transaction where id = ?";
    public static final String INSERT_TRANSACTION = "INSERT INTO transaction " +
            "(from_account_id, amount, currency_id, to_account_id, creation_date) VALUES (?, ?, ?, ?, ?)";

    @Inject
    public TransactionDTOImpl(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return dbUtil.executeOnlyReadQuery(GET_TRANSACTION_BY_ID, preparedStatement -> {
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
    public List<Transaction> getTransactions() {
        return dbUtil.executeOnlyReadQuery(GET_TRANSACTIONS, preparedStatement -> {
            List<Transaction> transactions = new ArrayList<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs != null){
                    while (rs.next()) {
                        transactions.add(fromResultSet(rs));
                    }
                }
            }
            return transactions;
        }).getResult();
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return dbUtil.executeQuery(INSERT_TRANSACTION, preparedStatement -> {
            preparedStatement.setLong(1, transaction.getFromAccountId());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setLong(3, transaction.getCurrency().getId());
            preparedStatement.setLong(4, transaction.getToAccountId());
            preparedStatement.setTimestamp(5, Timestamp.from(Instant.now()));

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
            transaction.setId(generatedId);
            return transaction;
        }).getResult();
    }

    private Transaction fromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setFromAccountId(rs.getLong("from_account_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setCurrency(Currency.getCurrencyById(rs.getLong("currency_id")));
        transaction.setToAccountId(rs.getLong("to_account_id"));
        transaction.setCreationDate(rs.getTimestamp("creation_date"));
        transaction.setLastUpdatedDate(rs.getTimestamp("last_update_date"));
        return transaction;
    }
}
