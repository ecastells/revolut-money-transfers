package com.revolut.moneytransfers.db;

import com.revolut.moneytransfers.error.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;

@Singleton
public class DBUtilImpl implements DBUtil {

    private static final Logger log = LoggerFactory.getLogger(DBUtilImpl.class);
    private DBConnection dbConnection;

    @Inject
    public DBUtilImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public <T> ResultExecution<T> executeOnlyReadQuery(String query, GenerateStatement<T> queryExecutor) {
        try (Connection con = dbConnection.getReadConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            return new ResultExecution(queryExecutor.initializeStatement(stmt));
        } catch (Exception th) {
            log.error("Unexpected exception happens {}", th);
            throw new ConnectionException(th);
        }
    }

    @Override
    public <T> ResultExecution<T> executeQuery(String query,  GenerateStatement<T> queryExecutor) {
        try (Connection con = dbConnection.getWriteConnection();
             PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ResultExecution resultExecution = new ResultExecution(queryExecutor.initializeStatement(stmt));
            con.commit();
            return resultExecution;
        } catch (Exception th) {
            log.error("Unexpected exception happens {}", th);
            throw new ConnectionException(th);
        }
    }

    @Override
    public void destroyConnection() {
        this.dbConnection.destroyConnection();
    }


}
