package com.revolut.moneytransfers.db;

import com.revolut.moneytransfers.config.Configuration;
import com.revolut.moneytransfers.error.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

@Singleton
public class H2Connection implements DBConnection {
    private static final Logger log = LoggerFactory.getLogger(H2Connection.class);
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/";
    private static BasicDataSource dataSource = new BasicDataSource();

    @Inject
    public H2Connection(Configuration config) {
            StringBuilder url = new StringBuilder(DB_URL);
            url.append(config.getDbName()).append(";INIT=RUNSCRIPT FROM 'classpath:schema-definition.sql'\\;RUNSCRIPT FROM 'classpath:data-load.sql';TRACE_LEVEL_FILE=4");
            dataSource.setUrl(url.toString());
            dataSource.setUsername(config.getUserDb());
            dataSource.setPassword(config.getUserPass());
            dataSource.setInitialSize(100);
            dataSource.setTestWhileIdle(true);
            dataSource.setDriverClassName(JDBC_DRIVER);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setPoolPreparedStatements(true);
            dataSource.setFastFailValidation(true);
            dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            dataSource.setTimeBetweenEvictionRunsMillis(30000);
    }

    @Override
    public Connection getReadConnection(){
        return getConnection(true);
    }

    @Override
    public Connection getWriteConnection(){
        return getConnection(false);
    }

    @Override
    public void destroyConnection() {
        if(dataSource != null){
            try {
                dataSource.close();
            } catch (SQLException e) {
                log.error("Error closing the datasource {}", e);
            }
        }
    }

    private Connection getConnection(boolean isReadOnly){
        Connection connection;
        try {
            connection = dataSource.getConnection();
            if (isReadOnly){
                connection.setReadOnly(true);
            } else {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            log.error("Error connection to the DB: {}", e);
            throw new ConnectionException(e);
        }
        return connection;
    }
}
