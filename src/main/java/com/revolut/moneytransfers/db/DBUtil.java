package com.revolut.moneytransfers.db;

import com.revolut.moneytransfers.model.Entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DBUtil {
    <T> ResultExecution<T> executeOnlyReadQuery(String query, GenerateStatement<T> queryExecutor);
    <T> ResultExecution<T> executeQuery(String query, GenerateStatement<T> queryExecutor);

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
