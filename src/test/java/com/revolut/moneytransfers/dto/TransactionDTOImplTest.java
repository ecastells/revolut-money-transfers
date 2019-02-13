package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.config.Config;
import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.error.ValidationException;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.CurrencyConversion;
import com.revolut.moneytransfers.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TransactionDTOImplTest {

    DBUtil dbUtilMock;
    Connection connectionMock;
    AccountDTO accountDTOMock;
    Config configMock;
    CurrencyConversionDTO currencyConversionDTOMock;
    TransactionDTOImpl transactionDTO;


    @Before
    public void setUp() {
        dbUtilMock = mock(DBUtil.class);
        connectionMock = mock(Connection.class);
        accountDTOMock = mock(AccountDTO.class);
        currencyConversionDTOMock = mock(CurrencyConversionDTO.class);
        configMock = mock(Config.class);
        transactionDTO = new TransactionDTOImpl(dbUtilMock, accountDTOMock, currencyConversionDTOMock, configMock);
    }

    @Test
    public void testListTransactionsWithoutStatusFilter(){
        List<Transaction> transactions = Arrays.asList(createTransaction(1L, 1L, BigDecimal.TEN, Currency.EUR, 2L, null, 0),
                createTransaction(2L, 2L, BigDecimal.ONE, Currency.USD, 1L, null, 0));
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transactions);

        when(dbUtilMock.executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTIONS), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);

        List<Transaction> transactionsResult = transactionDTO.getTransactions(null);

        assertNotNull(transactionsResult);
        assertEquals(2, transactionsResult.size());
        assertEquals(transactions, transactionsResult);

        // Check that this mock method was calling one time
        verify(dbUtilMock, times(1)).executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTIONS), any(DBUtil.GenerateStatement.class));
    }

    @Test
    public void testListTransactionsWithStatusRejected(){
        List<Transaction> transactions = Arrays.asList(createTransaction(1L, 1L, BigDecimal.TEN, Currency.EUR, 2L, Transaction.TransactionStatus.REJECTED, 0),
                createTransaction(2L, 2L, BigDecimal.ONE, Currency.USD, 1L, null, 0));
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transactions);

        when(dbUtilMock.executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTIONS_BY_STATUS), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);

        List<Transaction> transactionsResult = transactionDTO.getTransactions(Transaction.TransactionStatus.REJECTED);

        assertNotNull(transactionsResult);
        assertEquals(2, transactionsResult.size());
        assertEquals(transactions, transactionsResult);

        // Check that this mock method was calling one time
        verify(dbUtilMock, times(1)).executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTIONS_BY_STATUS), any(DBUtil.GenerateStatement.class));
    }



    @Test
    public void testGetTransactionById() {
        Transaction transaction = createTransaction(1L, 1L, BigDecimal.TEN, Currency.EUR, 2L, null, 0);
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTION_BY_ID), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        Transaction transactionResult = transactionDTO.getTransactionById(1L);

        assertNotNull(transactionResult);
        assertEquals(transaction, transactionResult);

        // Check that this mock method was calling one time
        verify(dbUtilMock, times(1)).executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTION_BY_ID), any(DBUtil.GenerateStatement.class));

    }

    @Test
    public void testCreateTransactionSuccessfullyWithSameCurrencyTransfer() throws Exception{
        Currency currencyTransaction = Currency.USD;
        Currency currencyAccount = Currency.USD;
        Long accountId = 1L;
        BigDecimal moneyToBeTransfer = BigDecimal.ONE;
        BigDecimal balance = BigDecimal.TEN;
        BigDecimal newBalance = balance.subtract(moneyToBeTransfer);

        Account account = createAccount(accountId, currencyAccount,balance);
        Transaction transaction = createTransaction(1L, accountId, moneyToBeTransfer, currencyTransaction, 2L, null, 0);
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(accountId))).thenReturn(account);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(accountId),  eq(newBalance))).thenReturn(account);

        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        doNothing().when(connectionMock).commit();
        doNothing().when(dbUtilMock).closeConnection(connectionMock);

        Transaction transactionResult = transactionDTO.createTransaction(transaction);

        assertNotNull(transactionResult);
        assertEquals(transaction, transactionResult);

        // Check that these mock methods were calling one time
        verify(dbUtilMock, times(1)).getConnection();
        verify(accountDTOMock, times(1)).getAccountToBeUpdate(eq(connectionMock), eq(accountId));
        verify(accountDTOMock, times(1)).updateAccountBalance(eq(connectionMock), eq(accountId), eq(newBalance));
        verify(dbUtilMock, times(1)).executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class));
        verify(connectionMock, times(1)).commit();
        verify(dbUtilMock, times(1)).closeConnection(connectionMock);
    }

    @Test
    public void testCreateTransactionSuccessfullyWithDifferentCurrencyTransfer() throws Exception{
        Currency currencyTransaction = Currency.EUR;
        Currency currencyAccount = Currency.USD;
        Long accountId = 1L;
        BigDecimal rateChange = BigDecimal.valueOf(0.21);
        BigDecimal moneyToBeTransfer = BigDecimal.ONE;
        BigDecimal balance = BigDecimal.TEN;
        BigDecimal moneyConversion = rateChange.multiply(moneyToBeTransfer);
        BigDecimal newBalance = balance.subtract(moneyConversion);

        CurrencyConversion currencyConversion = createCurrencyConversion(rateChange);
        Account account = createAccount(accountId, currencyAccount,balance);
        Transaction transaction = createTransaction(1L, accountId, moneyToBeTransfer, currencyTransaction, 2L, null, 0);
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(accountId))).thenReturn(account);

        when(currencyConversionDTOMock.getCurrencyConversion(eq(transaction.getCurrency()), eq(account.getCurrency()))).thenReturn(currencyConversion);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(accountId),  eq(newBalance))).thenReturn(account);

        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        doNothing().when(connectionMock).commit();
        doNothing().when(dbUtilMock).closeConnection(connectionMock);

        Transaction transactionResult = transactionDTO.createTransaction(transaction);

        assertNotNull(transactionResult);
        assertEquals(transaction, transactionResult);

        // Check that these mock methods were calling one time
        verify(dbUtilMock, times(1)).getConnection();
        verify(accountDTOMock, times(1)).getAccountToBeUpdate(eq(connectionMock), eq(accountId));
        verify(currencyConversionDTOMock, times(1)).getCurrencyConversion(eq(transaction.getCurrency()), eq(account.getCurrency()));
        verify(accountDTOMock, times(1)).updateAccountBalance(eq(connectionMock), eq(accountId), eq(newBalance));
        verify(dbUtilMock, times(1)).executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class));
        verify(connectionMock, times(1)).commit();
        verify(dbUtilMock, times(1)).closeConnection(connectionMock);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionFailureWhenTryToTransferMoreMoneyThanTheCurrentBalance() throws Exception{
        Currency currencyTransaction = Currency.USD;
        Currency currencyAccount = Currency.USD;
        Long accountId = 1L;
        BigDecimal moneyToBeTransfer = BigDecimal.TEN;
        BigDecimal balance = BigDecimal.ONE;

        Account account = createAccount(accountId, currencyAccount,balance);
        Transaction transaction = createTransaction(1L, accountId, moneyToBeTransfer, currencyTransaction, 2L, null, 0);

        doNothing().when(dbUtilMock).closeConnection(connectionMock);

        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(accountId))).thenReturn(account);

        doNothing().when(connectionMock).rollback();

        transactionDTO.createTransaction(transaction);
    }

    @Test (expected = ConnectionException.class)
    public void testCreateTransactionFailureWhenTryToCreateTheTransaction() throws Exception{
        Currency currencyTransaction = Currency.USD;
        Currency currencyAccount = Currency.USD;
        Long accountId = 1L;
        BigDecimal moneyToBeTransfer = BigDecimal.ONE;
        BigDecimal balance = BigDecimal.TEN;
        BigDecimal newBalance = balance.subtract(moneyToBeTransfer);

        Account account = createAccount(accountId, currencyAccount,balance);
        Transaction transaction = createTransaction(1L, 1L, moneyToBeTransfer, currencyTransaction, 2L, null, 0);
        DBUtil.ResultExecution<Account> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(accountId))).thenReturn(account);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(accountId),  eq(newBalance))).thenReturn(account);

        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenThrow(ConnectionException.class);
        doNothing().when(connectionMock).rollback();
        doNothing().when(dbUtilMock).closeConnection(connectionMock);

        Transaction transactionResult = transactionDTO.createTransaction(transaction);

        assertNotNull(transactionResult);
        assertEquals(transaction, transactionResult);

    }

    @Test
    public void testProcessTransactionSuccessfullyWithSameCurrencyTransfer() throws Exception{
        Currency currencyTransaction = Currency.USD;
        Currency currencyAccount = Currency.USD;
        Long accountId = 1L;
        BigDecimal moneyToBeTransfer = BigDecimal.ONE;
        BigDecimal balance = BigDecimal.TEN;
        BigDecimal newBalance = balance.add(moneyToBeTransfer);

        Account account = createAccount(accountId, currencyAccount,balance);
        Transaction transaction = createTransaction(1L, 1L, moneyToBeTransfer, currencyTransaction, accountId, null, 0);
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.GET_TRANSACTION_BY_ID_TO_BE_UPDATED), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(accountId))).thenReturn(account);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(accountId),  eq(newBalance))).thenReturn(account);
        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.UPDATE_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        doNothing().when(connectionMock).commit();
        doNothing().when(dbUtilMock).closeConnection(connectionMock);

        transactionDTO.processTransaction(transaction.getId());

        // Check that these mock methods were calling one time
        verify(dbUtilMock, times(1)).getConnection();
        verify(accountDTOMock, times(1)).getAccountToBeUpdate(eq(connectionMock), eq(accountId));
        verify(accountDTOMock, times(1)).updateAccountBalance(eq(connectionMock), eq(accountId), eq(newBalance));
        verify(dbUtilMock, times(1)).executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.GET_TRANSACTION_BY_ID_TO_BE_UPDATED), any(DBUtil.GenerateStatement.class));
        verify(connectionMock, times(1)).commit();
        verify(dbUtilMock, times(1)).closeConnection(connectionMock);
        verify(dbUtilMock, times(1)).executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.UPDATE_TRANSACTION), any(DBUtil.GenerateStatement.class));
    }

    @Test( expected = ConnectionException.class)
    public void testProcessTransactionFailureIncreasingTheRetryCreation() throws Exception{
        Currency currencyTransaction = Currency.USD;
        Currency currencyAccount = Currency.USD;
        Long accountId = 1L;
        BigDecimal moneyToBeTransfer = BigDecimal.ONE;
        BigDecimal balance = BigDecimal.TEN;
        BigDecimal newBalance = balance.add(moneyToBeTransfer);

        Account account = createAccount(accountId, currencyAccount,balance);
        Transaction transaction = createTransaction(1L, 1L, moneyToBeTransfer, currencyTransaction, accountId, null, 0);
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.GET_TRANSACTION_BY_ID_TO_BE_UPDATED), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(accountId))).thenReturn(account);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(accountId),  eq(newBalance))).thenThrow(ConnectionException.class);
        doNothing().when(connectionMock).rollback();
        when(configMock.getMaxRetryCreation()).thenReturn(3);
        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.UPDATE_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        doNothing().when(connectionMock).commit();
        doNothing().when(dbUtilMock).closeConnection(connectionMock);

        transactionDTO.processTransaction(transaction.getId());
    }


    @Test( expected = ConnectionException.class)
    public void testProcessTransactionAndMarkWithFailureStatus() throws Exception{
        Currency currencyTransaction = Currency.USD;
        Currency currencyAccount = Currency.USD;
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal moneyToBeTransfer = BigDecimal.ONE;
        BigDecimal balance = BigDecimal.TEN;
        BigDecimal newBalance = balance.add(moneyToBeTransfer);

        Account toAccount = createAccount(toAccountId, currencyAccount, balance);
        Account fromAccount = createAccount(fromAccountId, currencyAccount, balance);
        Transaction transaction = createTransaction(1L, fromAccountId, moneyToBeTransfer, currencyTransaction, toAccountId, null, 3);
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.GET_TRANSACTION_BY_ID_TO_BE_UPDATED), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(toAccountId))).thenReturn(toAccount);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(toAccountId),  eq(newBalance))).thenThrow(ConnectionException.class);
        doNothing().when(connectionMock).rollback();
        when(configMock.getMaxRetryCreation()).thenReturn(3);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(fromAccountId))).thenReturn(fromAccount);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(fromAccountId),  eq(newBalance))).thenReturn(fromAccount);
        when(dbUtilMock.executeQueryInTransaction(eq(connectionMock), eq(TransactionDTOImpl.UPDATE_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        doNothing().when(connectionMock).commit();
        doNothing().when(dbUtilMock).closeConnection(connectionMock);

        transactionDTO.processTransaction(transaction.getId());
    }


    private Transaction createTransaction(Long id, Long fromAccountId, BigDecimal amount, Currency currency, Long toAccountId, Transaction.TransactionStatus status, int retryCount){
        Transaction transaction = new Transaction();
        transaction.setToAccountId(id != null ? id : 1L);
        transaction.setFromAccountId(fromAccountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setToAccountId(toAccountId);
        transaction.setStatus(status);
        transaction.setRetryCreation(retryCount);
        return transaction;
    }

    private Account createAccount(Long id, Currency currency, BigDecimal balance){
        Account account = new Account();
        account.setId(id != null ? id : 1L);
        account.setOwner("owner");
        account.setCurrency(currency);
        account.setBalance(balance);
        return account;
    }

    private CurrencyConversion createCurrencyConversion(BigDecimal rateChange){
        CurrencyConversion currencyConversion = new CurrencyConversion();
        currencyConversion.setId(1L);
        currencyConversion.setRateChange(rateChange);
        return currencyConversion;
    }
}
