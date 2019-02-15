package com.revolut.moneytransfers.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DBUtil {
    /**
     * The method perform the query passed into the method with the {@link PreparedStatement} generated
     * The method also receive a boolean param to express if the query is readOnly or not.
     * This method responds to handle work with the connection, transaction and prepared statement life cycles
     *
     * @param readOnly a boolean which determine if the query is readOnly or not
     * @param query the query string which will be passed into Connection.preparedStatement method
     * @param queryExecutor the executor with only one method accepting {@link PreparedStatement} instance created
     * @return query result object with the only method <code>getResult</code> returns the result of queryExecutor
     */
    <T> ResultExecution<T> executeQuery(boolean readOnly, String query, GenerateStatement<T> queryExecutor);

    /**
     * The method perform the query passed into the method with the {@link PreparedStatement} generated
     * The method also receive a connection where it should not be committed, closed and rollback here.
     * This method responds to handle work with the connection, transaction and prepared statement life cycles
     *
     * @param con the connection which will be used to create a prepared statement
     * @param query the query string which will be passed into Connection.preparedStatement method
     * @param queryExecutor the executor with only one method accepting {@link PreparedStatement} instance created
     * @return query result object with the only method <code>getResult</code> returns the result of queryExecutor
     */
    <T> ResultExecution<T> executeQueryInTransaction(Connection con, String query, GenerateStatement<T> queryExecutor);

    /**
     * @return the database {@link Connection}
     */
    Connection getConnection();

    /**
     * Rollback a Transaction
     *
     * @param con to close
     */
    void rollback(Connection con);

    /**
     * Close a {@link Connection} from the database
     *
     * @param con to close
     */
    void closeConnection(Connection con);

    /**
     * Close a {@link PreparedStatement} from a Connection
     *
     * @param ps to close
     */
    void closePreparedStatement(PreparedStatement ps);

    interface GenerateStatement <T>{
        T initializeStatement (PreparedStatement preparedStatement) throws SQLException;
    }

    class ResultExecution<T> {
        private T result;

        public ResultExecution(T result) {
            this.result = result;
        }

        public T getResult() {
            return result;
        }
    }
}
