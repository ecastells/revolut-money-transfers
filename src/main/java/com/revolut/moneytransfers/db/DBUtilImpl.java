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
    public <T> ResultExecution<T> executeQuery(boolean readOnly, String query, GenerateStatement<T> queryExecutor) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dbConnection.getConnection();
            ResultExecution resultExecution;
            if(readOnly){
                stmt = con.prepareStatement(query);
                resultExecution = new ResultExecution(queryExecutor.initializeStatement(stmt));
            } else {
                stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                resultExecution = new ResultExecution(queryExecutor.initializeStatement(stmt));
                con.commit();
            }
            return resultExecution;
        } catch (Exception th) {
            log.error("Unexpected exception happens {}", th);
            rollback(con);
            throw new ConnectionException(th);
        } finally {
            closePreparedStatement(stmt);
            closeConnection(con);
        }
    }

    @Override
    public <T> ResultExecution<T> executeQueryInTransaction(Connection con, String query,  GenerateStatement<T> queryExecutor) {
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            return new ResultExecution(queryExecutor.initializeStatement(stmt));
        } catch (Exception th) {
            log.error("Unexpected exception happens {}", th);
            throw new ConnectionException(th);
        }
    }

    @Override
    public Connection getConnection() {
        return this.dbConnection.getConnection();
    }

    @Override
    public void rollback(Connection con){
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException e) {
                log.error("Exception rollback connection {}", e);
                throw new ConnectionException(e);
            }
        }
    }

    @Override
    public void closeConnection(Connection con){
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("Exception closing connection {}", e);
                throw new ConnectionException(e);
            }
        }
    }

    @Override
    public void closePreparedStatement(PreparedStatement ps){
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                log.error("Exception closing PreparedStatement {}", e);
                throw new ConnectionException(e);
            }
        }
    }

}
