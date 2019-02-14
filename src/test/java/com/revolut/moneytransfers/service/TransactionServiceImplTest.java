package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.TransactionDTO;
import com.revolut.moneytransfers.error.ValidationException;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class TransactionServiceImplTest {

    TransactionService transactionService;
    TransactionDTO transactionDTOMock;

    @Before
    public void setUp() {
        transactionDTOMock = mock(TransactionDTO.class);
        transactionService = new TransactionServiceImpl(transactionDTOMock);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithNullFromAccountId() {
        Transaction transaction = createTransaction(null, null, BigDecimal.ONE, Currency.ARG, 1L, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithNullToAccountId() {
        Transaction transaction = createTransaction(null, 1L, BigDecimal.ONE, Currency.ARG, null, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithNullCurrency() {
        Transaction transaction = createTransaction(null, 1L, BigDecimal.ONE, null, 2L, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithNullAmount() {
        Transaction transaction = createTransaction(null, 1L, null, Currency.ARG, 2L, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithSameAccountId() {
        Transaction transaction = createTransaction(null, 1L, BigDecimal.ONE, Currency.ARG, 1L, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithNegativeFromAccountId() {
        Transaction transaction = createTransaction(null, -1L, BigDecimal.ONE, Currency.ARG, 2L, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithNegativeToAccountId() {
        Transaction transaction = createTransaction(null, 1L, BigDecimal.ONE, Currency.ARG, -2L, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test (expected = ValidationException.class)
    public void testCreateTransactionWithNegativeAmount() {
        Transaction transaction = createTransaction(null, 1L, BigDecimal.valueOf(-1), Currency.ARG, 2L, Transaction.TransactionStatus.PENDING, 0);
        transactionService.createTransaction(transaction);
    }

    @Test
    public void testCreateTransactionSuccessfully() {
        Transaction transaction = createTransaction(null, 1L, BigDecimal.ONE, Currency.ARG, 2L, Transaction.TransactionStatus.PENDING, 0);
        when(transactionDTOMock.createTransaction(eq(transaction))).thenReturn(transaction);

        Transaction transactionResponse = transactionService.createTransaction(transaction);

        assertNotNull(transactionResponse);
        assertEquals(transaction, transactionResponse);
        verify(transactionDTOMock, times(1)).createTransaction(eq(transaction));
    }


    @Test
    public void testProcessTransactions() {
        Long transactionId = 1L;
        Transaction transaction = createTransaction(transactionId, 1L, BigDecimal.ONE, Currency.ARG, 2L, Transaction.TransactionStatus.PENDING, 0);
        when(transactionDTOMock.getTransactions(eq(Transaction.TransactionStatus.PENDING))).thenReturn(Arrays.asList(transaction));

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            assertEquals(transactionId, id);
            return null;
        }).when(transactionDTOMock).processTransaction(eq(transactionId));

        transactionService.processTransactions();

        verify(transactionDTOMock, times(1)).getTransactions(eq(Transaction.TransactionStatus.PENDING));
        verify(transactionDTOMock, times(1)).processTransaction(eq(transactionId));

    }

    private Transaction createTransaction(Long id, Long fromAccountId, BigDecimal amount, Currency currency, Long toAccountId, Transaction.TransactionStatus status, int retryCount){
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setFromAccountId(fromAccountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setToAccountId(toAccountId);
        transaction.setStatus(status);
        transaction.setRetryCreation(retryCount);
        return transaction;
    }
}