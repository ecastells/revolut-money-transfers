package com.revolut.moneytransfers.db;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.moneytransfers.config.InjectionConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import com.revolut.moneytransfers.model.Account;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.ResultSet;
public class DBUtilImplTest {

    private static DBUtil dbUtil;

    @BeforeClass
    public static void initTestData() {
        Injector injector = Guice.createInjector(new InjectionConfiguration());
        dbUtil = injector.getInstance(DBUtil.class);
    }

    @AfterClass
    public static void finishTestData() {
        dbUtil.destroyConnection();
    }

    @Test
    public void testDBConnection(){
        DBUtil.ResultExecution<ResultSet> resultSetResultExecution = dbUtil.executeOnlyReadQuery("SELECT 1", preparedStatement -> {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs;
            }
        });
        assertNotNull(resultSetResultExecution);
        assertNotNull(resultSetResultExecution.getResult());
    }

    @Test
    public void testInsertGetAndDelete(){
        // insert
        final String owner = "test";
        DBUtil.ResultExecution<Account> resultInsertion = dbUtil.executeQuery("INSERT INTO account (owner, balance, pendingTransfer, currency_id) VALUES ('"+owner+"', 0, 0, 1)", preparedStatement -> {
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
        DBUtil.ResultExecution<String> resultOwner = dbUtil.executeOnlyReadQuery("SELECT owner from account where id = ?", preparedStatement -> {
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
        DBUtil.ResultExecution<Integer> resultDelete = dbUtil.executeQuery("DELETE from account where id = ?", preparedStatement -> {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate();
        });
        assertNotNull(resultDelete);
        assertNotNull(resultDelete.getResult());
        assertEquals(Integer.valueOf(1), resultDelete.getResult());
    }
}


