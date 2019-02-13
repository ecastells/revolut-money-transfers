package com.revolut.moneytransfers.integration.db;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.revolut.moneytransfers.config.IngestConfigurationTest;
import com.revolut.moneytransfers.db.DBUtil;
import com.revolut.moneytransfers.model.Account;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.Connection;
import java.sql.ResultSet;
public class DBUtilImplTest {

    private static DBUtil dbUtil;

    @BeforeClass
    public static void initTestData() {
        Injector injector = Guice.createInjector(new IngestConfigurationTest());
        dbUtil = injector.getInstance(DBUtil.class);
    }

    @Test
    public void testDBConnection(){
        DBUtil.ResultExecution<ResultSet> resultSetResultExecution = dbUtil.executeQuery(true,"SELECT 1", preparedStatement -> {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs;
            }
        });
        assertNotNull(resultSetResultExecution);
        assertNotNull(resultSetResultExecution.getResult());
    }

    @Test
    public void testInsertGetAndDeleteWithoutTransaction(){
        // insert
        final String owner = "test";
        DBUtil.ResultExecution<Account> resultInsertion = dbUtil.executeQuery(false, "INSERT INTO account (owner, balance, currency_id) VALUES ('"+owner+"', 0, 1)", preparedStatement -> {
            Account account = new Account();
            int rows = preparedStatement.executeUpdate();
            Long generatedId = null;

            if (rows != 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getLong(1);
                    }
                }
            }
            account.setId(generatedId);
            return account;
        });
        assertNotNull(resultInsertion);
        assertNotNull(resultInsertion.getResult());
        final Long id = resultInsertion.getResult().getId();
        assertNotNull(id);

        // get
        DBUtil.ResultExecution<String> resultOwner = dbUtil.executeQuery(true,"SELECT owner from account where id = ?", preparedStatement -> {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs != null && rs.next()){
                    return rs.getString("owner");
                }
            }
            return null;
        });
        assertNotNull(resultOwner);
        assertNotNull(resultOwner.getResult());
        assertEquals(owner, resultOwner.getResult());

        // delete
        DBUtil.ResultExecution<Integer> resultDelete = dbUtil.executeQuery(false,"DELETE from account where id = ?", preparedStatement -> {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate();
        });
        assertNotNull(resultDelete);
        assertNotNull(resultDelete.getResult());
        assertEquals(Integer.valueOf(1), resultDelete.getResult());
    }

    @Test
    public void testInsertGetAndDeleteWithTransaction(){
        // insert
        final String owner = "test";
        Connection connection = dbUtil.getConnection();

        DBUtil.ResultExecution<Account> resultInsertion = dbUtil.executeQueryInTransaction(connection, "INSERT INTO account (owner, balance, currency_id) VALUES ('"+owner+"', 0, 1)", preparedStatement -> {
            Account account = new Account();
            int rows = preparedStatement.executeUpdate();
            Long generatedId = null;

            if (rows != 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getLong(1);
                    }
                }
            }
            account.setId(generatedId);
            return account;
        });
        assertNotNull(resultInsertion);
        assertNotNull(resultInsertion.getResult());
        final Long id = resultInsertion.getResult().getId();
        assertNotNull(id);

        // get
        DBUtil.ResultExecution<String> resultOwner = dbUtil.executeQueryInTransaction(connection,"SELECT owner from account where id = ?", preparedStatement -> {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs != null && rs.next()){
                    return rs.getString("owner");
                }
            }
            return null;
        });
        assertNotNull(resultOwner);
        assertNotNull(resultOwner.getResult());
        assertEquals(owner, resultOwner.getResult());

        // delete
        DBUtil.ResultExecution<Integer> resultDelete = dbUtil.executeQueryInTransaction(connection,"DELETE from account where id = ?", preparedStatement -> {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate();
        });
        assertNotNull(resultDelete);
        assertNotNull(resultDelete.getResult());
        assertEquals(Integer.valueOf(1), resultDelete.getResult());
    }
}


