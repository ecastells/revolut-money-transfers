package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.Entity;
import com.revolut.moneytransfers.model.Transaction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TransactionDTOImplTest {

    DBUtil dbUtilMock;
    Connection connectionMock;
    AccountDTO accountDTOMock;
    CurrencyConversionDTO currencyConversionDTOMock;
    TransactionDTOImpl transactionDTO;

    @Before
    public void setUp() {
        dbUtilMock = mock(DBUtil.class);
        connectionMock = mock(Connection.class);
        accountDTOMock = mock(AccountDTO.class);
        currencyConversionDTOMock = mock(CurrencyConversionDTO.class);
        transactionDTO = new TransactionDTOImpl(dbUtilMock, accountDTOMock, currencyConversionDTOMock);
    }

    @Test
    public void testListTransactions(){
        List<Transaction> transactions = Arrays.asList(createTransaction(1L, 1L, BigDecimal.TEN, Currency.EUR, 2L),
                createTransaction(2L, 2L, BigDecimal.ONE, Currency.USD, 1L));
        DBUtil.ResultExecution<Transaction> resultExecution = new DBUtil.ResultExecution(transactions);

        when(dbUtilMock.executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTIONS), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        List<Transaction> transactionsResult = transactionDTO.getTransactions();

        assertNotNull(transactionsResult);
        assertEquals(2, transactionsResult.size());
        assertEquals(transactions, transactionsResult);
        verify(dbUtilMock, times(1)).executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTIONS), any(DBUtil.GenerateStatement.class));
    }

    @Ignore
    public void testGetTransactionById() throws Exception{
        Currency currencyTransaction = Currency.USD;
        Currency currencyAccount = Currency.USD;
        Long accountId = 1L;
        BigDecimal moneyToBeTransfer = BigDecimal.ONE;
        BigDecimal balance = BigDecimal.TEN;
        BigDecimal newBalance = moneyToBeTransfer.subtract(balance);

        Account account = createAccount(accountId, currencyAccount,balance, BigDecimal.ZERO);
        Transaction transaction = createTransaction(1L, 1L, moneyToBeTransfer, currencyTransaction, 2L);
        DBUtil.ResultExecution<Account> resultExecution = new DBUtil.ResultExecution(transaction);

        doAnswer(invocation -> {
            return null;
        }).when(connectionMock).commit();

        doAnswer(invocation -> {
            return null;
        }).when(dbUtilMock).closeConnection(connectionMock);

     /*   doAnswer(invocation -> {
            return null;
        }).when(dbUtilMock).closePreparedStatement(connectionMock);
*/
        when(dbUtilMock.getConnection()).thenReturn(connectionMock);
        when(accountDTOMock.getAccountToBeUpdate(eq(connectionMock), eq(accountId))).thenReturn(account);
        when(accountDTOMock.updateAccountBalance(eq(connectionMock), eq(accountId),  eq(newBalance), eq(moneyToBeTransfer))).thenReturn(account);

        when(dbUtilMock.executeQuery(eq(true), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        Transaction transactionResult = transactionDTO.getTransactionById(1L);

        assertNotNull(transactionResult);
        assertEquals(transaction, transactionResult);
        verify(dbUtilMock, times(1)).executeQuery(eq(true), eq(TransactionDTOImpl.GET_TRANSACTION_BY_ID), any(DBUtil.GenerateStatement.class));
    }

    @Ignore
    public void testCreateAccountSuccessfully(){
        Transaction transaction = createTransaction(1L, 1L, BigDecimal.TEN, Currency.EUR, 2L);
        DBUtil.ResultExecution<Account> resultExecution = new DBUtil.ResultExecution(transaction);

        when(dbUtilMock.executeQuery(eq(false), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        Transaction transactionResult = transactionDTO.createTransaction(transaction);

        assertNotNull(transactionResult);
        assertEquals(transaction, transactionResult);
        verify(dbUtilMock, times(1)).executeQuery(eq(false), eq(TransactionDTOImpl.INSERT_TRANSACTION), any(DBUtil.GenerateStatement.class));
    }


    private Transaction createTransaction(Long id, Long fromAccountId, BigDecimal amount, Currency currency, Long toAccountId){
        Transaction transaction = new Transaction();
        transaction.setToAccountId(id != null ? id : 1L);
        transaction.setFromAccountId(fromAccountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setToAccountId(toAccountId);
        return transaction;
    }

    private Account createAccount(Long id, Currency currency, BigDecimal balance, BigDecimal pendingTransfer){
        Account account = new Account();
        account.setId(id != null ? id : 1L);
        account.setOwner("owner");
        account.setCurrency(currency);
        account.setBalance(balance);
        account.setPendingTransfer(pendingTransfer);
        return account;
    }
}
