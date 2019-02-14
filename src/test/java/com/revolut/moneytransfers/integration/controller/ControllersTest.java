package com.revolut.moneytransfers.integration.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.moneytransfers.config.Config;
import com.revolut.moneytransfers.config.IngestConfigurationTest;
import com.revolut.moneytransfers.controller.AccountController;
import com.revolut.moneytransfers.controller.TransactionController;
import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Account;
import com.revolut.moneytransfers.model.Currency;
import com.revolut.moneytransfers.model.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;
import spark.utils.IOUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ControllersTest {

    private static Config configurationTest = null;
    private static DBUtil dbUtil;

    @BeforeClass
    public static void beforeClass() {
        Injector injector = Guice.createInjector(new IngestConfigurationTest());
        configurationTest = injector.getInstance(Config.class);
        injector.getInstance(AccountController.class);
        injector.getInstance(TransactionController.class);
        dbUtil = injector.getInstance(DBUtil.class);
        Spark.awaitInitialization();
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    @Test
    public void testCreateNewAccountSuccessfully() {
        Account account = createAccount("emi", Currency.ARG, BigDecimal.TEN);
        TestResponse res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(account));
        Account accountCreated = generateAccount(res.body);

        assertEquals(201, res.status);
        assertNotNull(accountCreated);
        assertEquals("emi", accountCreated.getOwner());
        assertEquals(BigDecimal.TEN, accountCreated.getBalance());
        assertEquals(Currency.ARG, accountCreated.getCurrency());

        Long id = accountCreated.getId();
        assertNotNull(id);

        // deleting account created
        deleteAccount(id);
    }

    @Test
    public void testCreateAndRetrieveAccountSuccessfully() {
        Account account = createAccount("emi", Currency.ARG, BigDecimal.TEN);
        TestResponse res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(account));
        Account accountCreated = generateAccount(res.body);

        assertEquals(201, res.status);
        Long id = accountCreated.getId();
        assertNotNull(id);

        res = request("GET", configurationTest.getAccountPath() +"/"+id);
        Account accountRetrieved = generateAccount(res.body);

        assertEquals(200, res.status);
        assertNotNull(accountRetrieved);
        assertEquals(accountCreated.getOwner(), accountRetrieved.getOwner());
        assertEquals(accountCreated.getBalance().toBigInteger(), accountRetrieved.getBalance().toBigInteger());
        assertEquals(accountCreated.getCurrency(), accountRetrieved.getCurrency());

        // deleting account created
        deleteAccount(id);
    }

    @Test
    public void testCreateAndRetrieveAccountSuccessfullyFromTheList() {
        Account account = createAccount("emi", Currency.ARG, BigDecimal.TEN);
        TestResponse res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(account));
        Account accountCreated = generateAccount(res.body);

        assertEquals(201, res.status);
        Long id = accountCreated.getId();
        assertNotNull(id);

        res = request("GET", configurationTest.getAccountPath());
        List<Account> accountsRetrieved = generateAccountListAccounts(res.body);

        assertEquals(200, res.status);
        assertNotNull(accountsRetrieved);
        assertNotNull(accountsRetrieved.stream().filter(account1 -> id.equals(account1.getId())).findFirst().get());

        // deleting account created
        deleteAccount(id);
    }

    @Test
    public void testCreateNewTransactionSuccessfully() {
        Account fromAccount = createAccount("emi", Currency.EUR, BigDecimal.TEN);
        TestResponse res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(fromAccount));
        Account fromAccountCreated = generateAccount(res.body);
        assertEquals(201, res.status);

        Account toAccount = createAccount("gre", Currency.USD, BigDecimal.ONE);
        res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(toAccount));
        Account toAccountCreated = generateAccount(res.body);
        assertEquals(201, res.status);

        Transaction transaction = createTransaction(null, fromAccountCreated.getId(), BigDecimal.ONE, Currency.ARG, toAccountCreated.getId());
        res = request("POST", configurationTest.getTransactionPath(), new Gson().toJson(transaction));
        Transaction transactionCreated = generateTransaction(res.body);

        assertEquals(201, res.status);
        assertNotNull(transactionCreated);
        assertEquals(fromAccountCreated.getId(), transactionCreated.getFromAccountId());
        assertEquals(BigDecimal.ONE, transactionCreated.getAmount());
        assertEquals(Currency.ARG, transactionCreated.getCurrency());
        assertEquals(toAccountCreated.getId(), transactionCreated.getToAccountId());
        assertEquals(Transaction.TransactionStatus.PENDING, transactionCreated.getStatus());
        assertNotNull(transactionCreated.getCreationDate());
        assertEquals(Integer.valueOf(0), transactionCreated.getRetryCreation());

        Long id = transactionCreated.getId();
        assertNotNull(id);

        // deleting the accounts and transaction created
        deleteAccountsAndTransaction(id, new Long[]{toAccountCreated.getId(), fromAccountCreated.getId()});
    }

    @Test
    public void testCreateAndRetrieveTransactionSuccessfully() {
        Account fromAccount = createAccount("emi", Currency.EUR, BigDecimal.TEN);
        TestResponse res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(fromAccount));
        Account fromAccountCreated = generateAccount(res.body);
        assertEquals(201, res.status);

        Account toAccount = createAccount("gre", Currency.USD, BigDecimal.ONE);
        res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(toAccount));
        Account toAccountCreated = generateAccount(res.body);
        assertEquals(201, res.status);

        Transaction transaction = createTransaction(null, fromAccountCreated.getId(), BigDecimal.ONE, Currency.ARG, toAccountCreated.getId());
        res = request("POST", configurationTest.getTransactionPath(), new Gson().toJson(transaction));
        Transaction transactionCreated = generateTransaction(res.body);

        assertEquals(201, res.status);
        Long id = transactionCreated.getId();
        assertNotNull(id);

        res = request("GET", configurationTest.getTransactionPath()+"/"+id);
        Transaction transactionRetrieved = generateTransaction(res.body);

        assertEquals(200, res.status);
        assertNotNull(transactionRetrieved);
        assertEquals(id, transactionRetrieved.getId());
        assertEquals(transactionCreated.getFromAccountId(), transactionRetrieved.getFromAccountId());
        assertEquals(transactionCreated.getAmount().toBigInteger(), transactionRetrieved.getAmount().toBigInteger());
        assertEquals(transactionCreated.getCurrency(), transactionRetrieved.getCurrency());
        assertEquals(transactionCreated.getToAccountId(), transactionRetrieved.getToAccountId());
        assertEquals(transactionCreated.getStatus(), transactionRetrieved.getStatus());
        assertEquals(transactionCreated.getCreationDate(), transactionRetrieved.getCreationDate());
        assertEquals(transactionCreated.getRetryCreation(), transactionRetrieved.getRetryCreation());

        // deleting the accounts and transaction created
        deleteAccountsAndTransaction(id, new Long[]{toAccountCreated.getId(), fromAccountCreated.getId()});
    }

    @Test
    public void testCreateAndRetrieveTransactionSuccessfullyFromTheList() {
        Account fromAccount = createAccount("emi", Currency.EUR, BigDecimal.TEN);
        TestResponse res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(fromAccount));
        Account fromAccountCreated = generateAccount(res.body);
        assertEquals(201, res.status);

        Account toAccount = createAccount("gre", Currency.USD, BigDecimal.ONE);
        res = request("POST", configurationTest.getAccountPath(), new Gson().toJson(toAccount));
        Account toAccountCreated = generateAccount(res.body);
        assertEquals(201, res.status);

        Transaction transaction = createTransaction(null, fromAccountCreated.getId(), BigDecimal.ONE, Currency.ARG, toAccountCreated.getId());
        res = request("POST", configurationTest.getTransactionPath(), new Gson().toJson(transaction));
        Transaction transactionCreated = generateTransaction(res.body);

        assertEquals(201, res.status);
        Long id = transactionCreated.getId();
        assertNotNull(id);

        res = request("GET", configurationTest.getTransactionPath());
        List<Transaction> transactionsRetrieved = generateTransactionListAccounts(res.body);

        assertEquals(200, res.status);
        assertNotNull(transactionsRetrieved);
        assertNotNull(transactionsRetrieved.stream().filter(transaction1 -> id.equals(transaction1.getId())).findFirst().get());

        // deleting the accounts and transaction created
        deleteAccountsAndTransaction(id, new Long[]{toAccountCreated.getId(), fromAccountCreated.getId()});
    }

    private void deleteAccount(Long id) {
        DBUtil.ResultExecution<Integer> resultDelete = dbUtil.executeQuery(false, "DELETE from account where id = ?", preparedStatement -> {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate();
        });
        assertNotNull(resultDelete);
        assertNotNull(resultDelete.getResult());
        assertEquals(Integer.valueOf(1), resultDelete.getResult());
    }


    private Account createAccount(String owner, Currency currency, BigDecimal balance){
        Account account = new Account();
        account.setOwner(owner);
        account.setCurrency(currency);
        account.setBalance(balance);
        return account;
    }

    private TestResponse request(String method, String path, String body) {
        OutputStream os = null;
        OutputStreamWriter osw = null;
        try {
            URL url = new URL("http://localhost:" + configurationTest.getWebPort() + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            os = connection.getOutputStream();
            osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(body);
            osw.flush();
            connection.connect();
            String response = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), response);
        } catch (IOException e) {
            fail("Sending request failed: " + e.getMessage());
            return null;
        } finally {
            if (osw != null){
                try {
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private TestResponse request(String method, String path) {
        try {
            URL url = new URL("http://localhost:" + configurationTest.getWebPort() + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.connect();
            String body = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), body);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

    private static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }

    private Account generateAccount(String body) {
        return new Gson().fromJson(body, Account.class);
    }

    private List<Account> generateAccountListAccounts(String body) {
        Type targetClassType = new TypeToken<ArrayList<Account>>() { }.getType();
        return new Gson().fromJson(body, targetClassType);
    }

    private void deleteAccountsAndTransaction(Long transactionId, Long[] accountsIds) {
        DBUtil.ResultExecution<Integer> resultDelete = dbUtil.executeQuery(false, "DELETE from transaction where id = ?", preparedStatement -> {
            preparedStatement.setLong(1, transactionId);
            return preparedStatement.executeUpdate();
        });
        assertNotNull(resultDelete);
        assertNotNull(resultDelete.getResult());
        assertEquals(Integer.valueOf(1), resultDelete.getResult());

        resultDelete = dbUtil.executeQuery(false, "DELETE from account where id in (?, ?)", preparedStatement -> {
            //  Array array = preparedStatement.getConnection().createArrayOf("VARCHAR", accountsId);
            //statement.setArray(1, array);

            preparedStatement.setLong(1, accountsIds[0]);
            preparedStatement.setLong(2, accountsIds[1]);
            return preparedStatement.executeUpdate();
        });
        assertNotNull(resultDelete);
        assertNotNull(resultDelete.getResult());
        assertEquals(Integer.valueOf(2), resultDelete.getResult());

    }

    private Transaction createTransaction(Long id, Long fromAccountId, BigDecimal amount, Currency currency, Long toAccountId){
        Transaction transaction = new Transaction();
        transaction.setId(id != null ? id : 1L);
        transaction.setFromAccountId(fromAccountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setToAccountId(toAccountId);
        return transaction;
    }

    private Transaction generateTransaction(String body) {
        return new Gson().fromJson(body, Transaction.class);
    }

    private List<Transaction> generateTransactionListAccounts(String body) {
        Type targetClassType = new TypeToken<ArrayList<Transaction>>() { }.getType();
        return new Gson().fromJson(body, targetClassType);
    }
}
