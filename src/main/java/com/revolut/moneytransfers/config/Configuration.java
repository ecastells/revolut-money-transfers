package com.revolut.moneytransfers.config;

import com.revolut.moneytransfers.controller.GenericController;
import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.error.ResponseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.inject.Singleton;

/**
 * The configuration class. All configuration for the application an here.
 * dbUser:
 */
@Singleton
public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private String dbUser;
    private String dbPass;
    private String dbName;
    private int dbPoolSize;
    private int webPort;
    private String accountPath;
    private String transactionPath;

    public Configuration() {
        this.dbUser = "sa";
        this.dbPass = "";
        this.dbName = "MoneyTransfer";
        this.dbPoolSize = 100;
        this.webPort = 8080;
        this.accountPath = "/account";
        this.transactionPath = "/transaction";

        Spark.port(webPort);
    }

    public String getAccountPath() {
        return accountPath;
    }

    public String getTransactionPath() {
        return transactionPath;
    }

    public int getWebPort() {
        return webPort;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public String getDbName() {
        return dbName;
    }

    public int getDbPoolSize() {
        return dbPoolSize;
    }
}
