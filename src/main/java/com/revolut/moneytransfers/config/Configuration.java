package com.revolut.moneytransfers.config;

import com.revolut.moneytransfers.controller.GenericController;
import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.error.ResponseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;
import spark.Spark;

import javax.inject.Singleton;

/**
 * The configuration class. All configuration for the application an here.
 * dbUser:
 */
@Singleton
public class Configuration extends Config{
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    public Configuration(int webPort, int threadPool, String dbName) {
        super(webPort, threadPool, Service.ignite().port(webPort).threadPool(threadPool), "sa", "", dbName, 100);
        //Spark.port(webPort);
    }

  /*  public String getAccountPath() {
        return accountPath;
    }

    public String getTransactionPath() {
        return transactionPath;
    }

    public int getWebPort() {
        return webPort;
    }

    public Service getService() {
        return service;
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
    }*/
}
