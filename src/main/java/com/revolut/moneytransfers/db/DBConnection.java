package com.revolut.moneytransfers.db;

import java.sql.Connection;

public interface DBConnection {
    Connection getReadConnection();
    Connection getWriteConnection();
    void destroyConnection();
}
