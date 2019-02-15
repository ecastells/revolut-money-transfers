package com.revolut.moneytransfers.db;

import com.revolut.moneytransfers.config.Config;
import com.revolut.moneytransfers.error.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Implementation Class of the H2 Database connection. The DbName, DbUser, DbPass and DbPoolSize can be configured in order to
 * be different for each environment
 *
 */
@Singleton
public class H2Connection implements DBConnection {
    private static final Logger log = LoggerFactory.getLogger(H2Connection.class);
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:";
    private static BasicDataSource dataSource = new BasicDataSource();

    @Inject
    public H2Connection(Config config) {
            StringBuilder url = new StringBuilder(DB_URL + config.getDbName());
            url.append(config.getDbName()).append(";INIT=RUNSCRIPT FROM 'classpath:schema-definition.sql'\\;RUNSCRIPT FROM 'classpath:data-load.sql';MVCC=TRUE;LOCK_TIMEOUT=30000");
            dataSource.setUrl(url.toString());
            dataSource.setUsername(config.getDbUser());
            dataSource.setPassword(config.getDbPass());
            dataSource.setInitialSize(config.getDbPoolSize());
            dataSource.setMaxTotal(config.getDbPoolSize());
            dataSource.setMaxIdle(config.getDbPoolSize());
            dataSource.setMinIdle(2);
            dataSource.setTestWhileIdle(true);
            dataSource.setDriverClassName(JDBC_DRIVER);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setPoolPreparedStatements(true);
            dataSource.setFastFailValidation(true);
            dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            dataSource.setTimeBetweenEvictionRunsMillis(30000);
            dataSource.setDefaultAutoCommit(false);
    }

    @Override
    public Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Error connection to the DB: {}", e);
            throw new ConnectionException(e);
        }
    }
}
