package com.revolut.moneytransfers.db;

import com.revolut.moneytransfers.config.Configuration;
import com.revolut.moneytransfers.error.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class H2Connection implements DBConnection {
    private static final Logger log = LoggerFactory.getLogger(H2Connection.class);
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/";
    private Connection connection;

    @Inject
    public H2Connection(Configuration config) {
        try {
            Class.forName(JDBC_DRIVER);
            StringBuilder url = new StringBuilder(DB_URL);
            url.append(config.getDbName()).append(";INIT=RUNSCRIPT FROM 'classpath:schema-definition.sql'\\;RUNSCRIPT FROM 'classpath:data-load.sql';TRACE_LEVEL_FILE=4");
            connection = DriverManager.getConnection(url.toString(),config.getUserDb(),config.getUserPass());
        } catch (ClassNotFoundException | SQLException e) {
            log.error("Error connection to the DB: {}", e);
            throw new ConnectionException(e);
        }
    }

    @Override
    public Connection getReadConnection(){
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            log.error("Error connection to the DB: {}", e);
            throw new ConnectionException(e);
        }
        return connection;
    }

    @Override
    public Connection getWriteConnection(){
        try {
            connection.setReadOnly(true);
        } catch (SQLException e) {
            log.error("Error connection to the DB: {}", e);
            throw new ConnectionException(e);
        }
        return connection;
    }
}
