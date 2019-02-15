package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.config.Config;
import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.error.ValidationException;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.CurrencyConversion;
import com.revolut.moneytransfers.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Implementation Class of the {@link TransactionDTO} to perform actions over the database for {@link Transaction}
 *
 */

@Singleton
public class TransactionDTOImpl implements TransactionDTO, GenericDTO<Transaction> {
    private static final Logger log = LoggerFactory.getLogger(TransactionDTOImpl.class);

    private DBUtil dbUtil;
    CurrencyConversionDTO currencyConversionDTO;
    AccountDTO accountDTO;
    Config configuration;

    public static final String GET_TRANSACTIONS = "SELECT * FROM transaction";
    public static final String GET_TRANSACTIONS_BY_STATUS = GET_TRANSACTIONS + " where status = ?";
    public static final String GET_TRANSACTION_BY_ID = "SELECT * FROM transaction where id = ?";
    public static final String GET_TRANSACTION_BY_ID_TO_BE_UPDATED = GET_TRANSACTION_BY_ID + " FOR UPDATE";
    public static final String INSERT_TRANSACTION = "INSERT INTO transaction " +
            "(from_account_id, amount, currency_id, to_account_id, status, creation_date, retry_creation) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_TRANSACTION = "UPDATE transaction SET status = ?, retry_creation = ?, last_update_date = ? WHERE id = ?";

    @Inject
    public TransactionDTOImpl(DBUtil dbUtil, AccountDTO accountDTO, CurrencyConversionDTO currencyConversionDTO, Config configuration) {
        this.dbUtil = dbUtil;
        this.accountDTO = accountDTO;
        this.currencyConversionDTO = currencyConversionDTO;
        this.configuration = configuration;
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

            // 1- Get the origin account to be updated
            Account fromAccount = accountDTO.getAccountToBeUpdate(connection, transaction.getFromAccountId());

            // 2- Generate Currency Conversion
            BigDecimal moneyToTransfer = getMoneyConversion(transaction, fromAccount);

            BigDecimal newBalance = fromAccount.getBalance().subtract(moneyToTransfer);

            // 3 - Verify that the origin account has enough money before creating the transaction
            if (newBalance.compareTo(BigDecimal.ZERO) < 0){
                throw new ValidationException("The following account does not have enough money");
            }

            // 4 - Update the current Account
            accountDTO.updateAccountBalance(connection, fromAccount.getId(), newBalance);

            // 5 - Create the transaction
            transactionResponse = createTransaction(connection, transaction);

            connection.commit();

            log.debug("Transaction Created Successfully");

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

    @Override
    public void processTransaction(Long id) {
        Connection connection = null;

        try {
            connection = dbUtil.getConnection();

            // 1- Get the transaction to be updated
            Transaction transaction = getTransactionByIdToBeUpdated(connection, id);

            // 2 - Get the destination account to be updated
            Account toAccount = accountDTO.getAccountToBeUpdate(connection, transaction.getToAccountId());

            // 3 - Generate Currency Conversion
            BigDecimal moneyToReceive = getMoneyConversion(transaction, toAccount);

            // 4 - Update the destination account
            BigDecimal newBalance = toAccount.getBalance().add(moneyToReceive);
            accountDTO.updateAccountBalance(connection, toAccount.getId(), newBalance);

            // 5 - Update Transaction
            updateTransaction(connection, transaction.getId(), transaction.getRetryCreation(), Transaction.TransactionStatus.CONFIRMED);

            connection.commit();

            log.debug("Transaction Processed Successfully");

        } catch (RuntimeException e){
            dbUtil.rollback(connection);
            increaseTransactionRetryCreation(connection != null ? connection : dbUtil.getConnection(), id);
            throw e;
        } catch (SQLException e) {
            dbUtil.rollback(connection);
            increaseTransactionRetryCreation(connection, id);
            throw new ConnectionException(e);
        } finally {
            dbUtil.closeConnection(connection);
        }
    }

    private void increaseTransactionRetryCreation(Connection connection, Long id){
        try {
            // 1 - Get the transaction to be updated
            Transaction transaction = getTransactionByIdToBeUpdated(connection, id);

            Integer retryCreation = transaction.getRetryCreation();
            Transaction.TransactionStatus status;

            if (retryCreation < configuration.getMaxRetryCreation()){
                // 2A- Only Increase the retry creation in order to try to transfer again
                status = transaction.getStatus();
            } else {
                // 2B - Get the origin account to be updated
                Account fromAccount = accountDTO.getAccountToBeUpdate(connection, transaction.getFromAccountId());

                // 3B - Generate Currency Conversion
                BigDecimal moneyToReturn = getMoneyConversion(transaction, fromAccount);

                // 4B - Return the money to origin account
                BigDecimal newBalance = fromAccount.getBalance().add(moneyToReturn);
                accountDTO.updateAccountBalance(connection, fromAccount.getId(), newBalance);

                status = Transaction.TransactionStatus.REJECTED;
            }

            // 5 - Update the retryCreation and status of the transaction
            updateTransaction(connection, transaction.getId(), ++retryCreation, status);

            connection.commit();

        } catch (SQLException | RuntimeException e) {
            dbUtil.rollback(connection);
            log.warn("Error increasing the retry creation for the transaction: {}", e);
        }
    }


    private BigDecimal getMoneyConversion(Transaction transaction, Account toAccount) {
        BigDecimal moneyConversion;
        if (toAccount.getCurrency().equals(transaction.getCurrency())) {
            moneyConversion = transaction.getAmount();
        } else {
            CurrencyConversion currencyConversion = currencyConversionDTO.getCurrencyConversion(transaction.getCurrency(), toAccount.getCurrency());
            moneyConversion = currencyConversion != null ? transaction.getAmount().multiply(currencyConversion.getRateChange()) : BigDecimal.ZERO;
        }
        return moneyConversion;
    }

    protected Transaction getTransactionByIdToBeUpdated(Connection con, Long id) {
        return dbUtil.executeQueryInTransaction(con, GET_TRANSACTION_BY_ID_TO_BE_UPDATED, preparedStatement -> {
            preparedStatement.setLong(1, id);
            return getEntity(preparedStatement);
        }).getResult();
    }


    protected Transaction createTransaction(Connection con, Transaction transaction) {
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setCreationDate(Timestamp.from(Instant.now()));
        transaction.setRetryCreation(0);

        return dbUtil.executeQueryInTransaction(con, INSERT_TRANSACTION, preparedStatement -> {
            preparedStatement.setLong(1, transaction.getFromAccountId());
            preparedStatement.setBigDecimal(2, transaction.getAmount());
            preparedStatement.setLong(3, transaction.getCurrency().getId());
            preparedStatement.setLong(4, transaction.getToAccountId());
            preparedStatement.setString(5, transaction.getStatus().name());
            preparedStatement.setTimestamp(6, transaction.getCreationDate());
            preparedStatement.setInt(7, transaction.getRetryCreation());
            return !insertEntity(transaction, preparedStatement) ? null : transaction;
        }).getResult();
    }

    protected Transaction updateTransaction(Connection con, Long transactionId, Integer retryCreation, Transaction.TransactionStatus status) {
        return dbUtil.executeQueryInTransaction(con, UPDATE_TRANSACTION, preparedStatement -> {
            Timestamp lastUpdated = Timestamp.from(Instant.now());
            preparedStatement.setString(1, status.name());
            preparedStatement.setInt(2, retryCreation);
            preparedStatement.setTimestamp(3, lastUpdated);
            preparedStatement.setLong(4, transactionId);

            Transaction transaction = new Transaction();
            transaction.setId(transactionId);
            transaction.setRetryCreation(retryCreation);
            transaction.setStatus(status);
            transaction.setLastUpdatedDate(lastUpdated);
            return !updateEntity(transaction, preparedStatement) ? null : transaction;
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
        transaction.setRetryCreation(rs.getInt("retry_creation"));
        return transaction;
    }
}
