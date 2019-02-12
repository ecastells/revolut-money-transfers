package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.error.ValidationException;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.CurrencyConversion;
import com.revolut.moneytransfers.model.Transaction;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Singleton
public class TransactionDTOImpl implements TransactionDTO, GenericDTO<Transaction> {

    private DBUtil dbUtil;
    CurrencyConversionDTO currencyConversionDTO;
    AccountDTO accountDTO;

    public static final String GET_TRANSACTIONS = "SELECT * FROM transaction";
    public static final String GET_TRANSACTIONS_BY_STATUS = GET_TRANSACTIONS + " where status = ?";
    public static final String GET_TRANSACTION_BY_ID = "SELECT * FROM transaction where id = ?";
    public static final String INSERT_TRANSACTION = "INSERT INTO transaction " +
            "(from_account_id, amount, currency_id, to_account_id, status, creation_date) VALUES (?, ?, ?, ?, ?, ?)";

    @Inject
    public TransactionDTOImpl(DBUtil dbUtil, AccountDTO accountDTO, CurrencyConversionDTO currencyConversionDTO) {
        this.dbUtil = dbUtil;
        this.accountDTO = accountDTO;
        this.currencyConversionDTO = currencyConversionDTO;
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return dbUtil.executeQuery(true, GET_TRANSACTION_BY_ID, preparedStatement -> {
            preparedStatement.setLong(1, id);
            return getEntity(preparedStatement);
        }).getResult();
    }

    @Override
    public List<Transaction> getTransactions(Transaction.TransactionStatus status) {
        if(status == null){
            return dbUtil.executeQuery(true, GET_TRANSACTIONS, this::getListEntities).getResult();
        } else {
            return dbUtil.executeQuery(true, GET_TRANSACTIONS_BY_STATUS, preparedStatement -> {
                preparedStatement.setString(1, status.name());
                return getListEntities(preparedStatement);
            }).getResult();
        }
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        Transaction transactionResponse;
        Connection connection = null;

        try {
            connection = dbUtil.getConnection();

            // 1- Get the account
            Account fromAccount = accountDTO.getAccountToBeUpdate(connection, transaction.getFromAccountId());

            // 2- Generate Currency Conversion
            BigDecimal moneyToTransfer;
            if (fromAccount.getCurrency().equals(transaction.getCurrency())){
                moneyToTransfer = transaction.getAmount();
            } else {
                CurrencyConversion currencyConversion = currencyConversionDTO.getCurrencyConversion(transaction.getCurrency(), fromAccount.getCurrency());
                moneyToTransfer = currencyConversion != null ?  transaction.getAmount().multiply(currencyConversion.getRateChange()) : BigDecimal.ZERO;
            }

            BigDecimal newBalance = fromAccount.getBalance().subtract(moneyToTransfer);

            if (newBalance.compareTo(BigDecimal.ZERO) < 0){
                throw new ValidationException("The following account does not have enough money");
            }

            if (moneyToTransfer.compareTo(BigDecimal.ZERO) < 0){
                throw new ConnectionException("There was a problem getting the currency conversion");
            }

            // 3 - Update the current Account
            accountDTO.updateAccountBalance(connection, fromAccount.getId(), newBalance, moneyToTransfer);

            // 4 - Create the transaction
            transactionResponse = createTransaction(connection, transaction);

            connection.commit();

       } catch (RuntimeException e){
            dbUtil.rollback(connection);
            throw e;
        } catch (SQLException e) {
            dbUtil.rollback(connection);
            throw new ConnectionException(e);
        } finally {
            dbUtil.closeConnection(connection);
        }
       return transactionResponse;
    }

    protected Transaction createTransaction(Connection con, Transaction transaction) {
        return dbUtil.executeQueryInTransaction(con, INSERT_TRANSACTION, preparedStatement -> {
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
        transaction.setStatus(Transaction.TransactionStatus.valueOf(rs.getString("status")));
        transaction.setCreationDate(rs.getTimestamp("creation_date"));
        transaction.setLastUpdatedDate(rs.getTimestamp("last_update_date"));
        return transaction;
    }
}
