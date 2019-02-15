package com.revolut.moneytransfers.config;

import spark.Service;

/**
 * This Class contains the configuration of the application
 *
 */
// TODO - Read those values from property file
public abstract class Config {
    private String dbUser; // The database user name
    private String dbPass; // The database password name
    private String dbName; // The database name
    private int dbPoolSize; // The database max pool size
    private int webPort; // The web port of the application
    private Service service; // The service used for different environment (prod and test)
    private int threadPool; // the thread pool of the server
    private String accountPath; // the path of the account operation
    private String transactionPath; // the path of the transaction operation
    private Integer maxRetryCreation; // the max number of retry creation

    public Config(int webPort, int threadPool,  Service service, String dbUser, String dbPass, String dbName, int dbPoolSize) {
        this.webPort = webPort;
        this.threadPool = threadPool;
        this.service = service;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbName = dbName;
        this.dbPoolSize = dbPoolSize;
        this.accountPath = "/account";
        this.transactionPath = "/transaction";
        this.maxRetryCreation = 3;
    }

    public int getWebPort() {
        return webPort;
    }

    public int getThreadPool() {
        return threadPool;
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
    }

    public String getAccountPath() {
        return accountPath;
    }

    public String getTransactionPath() {
        return transactionPath;
    }

    public Integer getMaxRetryCreation() {
        return maxRetryCreation;
    }
}
