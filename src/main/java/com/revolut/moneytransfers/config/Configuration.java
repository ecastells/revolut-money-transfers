package com.revolut.moneytransfers.config;

import javax.inject.Singleton;

@Singleton
public class Configuration {
    private String userDb;
    private String userPass;
    private String dbName;
    private int webPort;
    private String accountPath;

    public Configuration() {
        this.userDb = "sa";
        this.userPass = "";
        this.dbName = "MoneyTransfer";
        this.webPort = 8080;
        this.accountPath = "/account";
    }

    public String getAccountPath() {
        return accountPath;
    }

    public int getWebPort() {
        return webPort;
    }

    public String getUserDb() {
        return userDb;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getDbName() {
        return dbName;
    }
}
