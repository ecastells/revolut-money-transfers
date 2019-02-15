package com.revolut.moneytransfers.db;

import java.sql.Connection;

public interface DBConnection {
    /**
     * @return the database {@link Connection}
     */
    Connection getConnection();
}
