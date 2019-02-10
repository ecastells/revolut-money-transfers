package com.revolut.moneytransfers.dto;

import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class AccountDTOImplTest {

    DBUtil dbUtilMock;

    AccountDTOImpl accountDTO;

    @Before
    public void setUp() {
        dbUtilMock = mock(DBUtil.class);
        accountDTO = new AccountDTOImpl(dbUtilMock);
    }

    @Test
    public void testListAccount(){
        List<Account> accounts = Arrays.asList(createAccount(1L, "emi"), createAccount(2L, "gre"));
        DBUtil.ResultExecution<Account> resultExecution = new DBUtil.ResultExecution(accounts);

        when(dbUtilMock.executeQuery(eq(true), eq(accountDTO.GET_ACCOUNTS), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        List<Account> accountsResult = accountDTO.getAccounts();

        assertNotNull(accountsResult);
        assertEquals(2, accountsResult.size());
        assertEquals(accounts, accountsResult);
        verify(dbUtilMock, times(1)).executeQuery(eq(true), eq(accountDTO.GET_ACCOUNTS), any(DBUtil.GenerateStatement.class));
    }

    @Test
    public void testGetAccountById(){
        Account account = createAccount(1L, "emi");
        DBUtil.ResultExecution<Account> resultExecution = new DBUtil.ResultExecution(account);

        when(dbUtilMock.executeQuery(eq(true), eq(accountDTO.GET_ACCOUNT_BY_ID), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        Account accountResult = accountDTO.getAccount(1L);

        assertNotNull(accountResult);
        assertEquals(account, accountResult);
        verify(dbUtilMock, times(1)).executeQuery(eq(true), eq(accountDTO.GET_ACCOUNT_BY_ID), any(DBUtil.GenerateStatement.class));
    }

    @Test
    public void testCreateAccount(){
        Account account = createAccount(1L, "emi");
        DBUtil.ResultExecution<Account> resultExecution = new DBUtil.ResultExecution(account);

        when(dbUtilMock.executeQuery(eq(false), eq(accountDTO.INSERT_ACCOUNT), any(DBUtil.GenerateStatement.class))).thenReturn(resultExecution);
        Account accountResult = accountDTO.createAccount(account);

        assertNotNull(accountResult);
        assertEquals(account, accountResult);
        verify(dbUtilMock, times(1)).executeQuery(eq(false), eq(accountDTO.INSERT_ACCOUNT), any(DBUtil.GenerateStatement.class));
    }


    private Account createAccount(Long id, String owner){
        Account account = new Account();
        account.setId(id != null ? id : 1L);
        account.setOwner(owner);
        account.setCurrency(Currency.ARG);
        account.setBalance(BigDecimal.ONE);
        account.setPendingTransfer(BigDecimal.ZERO);
        return account;
    }
}
