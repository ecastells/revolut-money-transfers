package com.revolut.moneytransfers.config;

import spark.Service;

public abstract class Config {
    private String dbUser;
    private String dbPass;
    private String dbName;
    private int dbPoolSize;
    private int webPort;
    private Service service;
    private int threadPool;
    private String accountPath;
    private String transactionPath;

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
}
