package com.revolut.moneytransfers.service;

import com.revolut.moneytransfers.dto.AccountDTO;
import com.revolut.moneytransfers.error.ValidationException;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class AccountServiceImplTest {

    AccountService accountService;
    AccountDTO accountDTOMock;

    @Before
    public void setUp() {
        accountDTOMock = mock(AccountDTO.class);
        accountService = new AccountServiceImpl(accountDTOMock);
    }

    @Test (expected = ValidationException.class)
    public void testCreateAccountWithNullOwner() {
        Account account = createAccount(null, null, Currency.ARG, BigDecimal.ONE);
        accountService.createAccount(account);
    }

    @Test (expected = ValidationException.class)
    public void testCreateAccountWithEmptyOwner() {
        Account account = createAccount(null, "  ", Currency.ARG, BigDecimal.ONE);
        accountService.createAccount(account);
    }

    @Test (expected = ValidationException.class)
    public void testCreateAccountWithNullCurrency() {
        Account account = createAccount(null, "owner", null, BigDecimal.ONE);
        accountService.createAccount(account);
    }

    @Test (expected = ValidationException.class)
    public void testCreateAccountWithNullBalance() {
        Account account = createAccount(null, "owner", Currency.ARG, null);
        accountService.createAccount(account);
    }

    @Test (expected = ValidationException.class)
    public void testCreateAccountWithNegativeBalance() {
        Account account = createAccount(null, "owner", Currency.ARG, BigDecimal.valueOf(-1));
        accountService.createAccount(account);
    }

    @Test
    public void testCreateAccountSuccessfully() {
        Account account = createAccount(null, "owner", Currency.ARG, BigDecimal.ONE);
        when(accountDTOMock.createAccount(eq(account))).thenReturn(account);

        Account accountResponse = accountService.createAccount(account);

        assertNotNull(accountResponse);
        assertEquals(account, accountResponse);
        verify(accountDTOMock, times(1)).createAccount(eq(account));
    }

    private Account createAccount(Long id, String owner, Currency currency, BigDecimal balance){
        Account account = new Account();
        account.setId(id);
        account.setOwner(owner);
        account.setCurrency(currency);
        account.setBalance(balance);
        return account;
    }
}
