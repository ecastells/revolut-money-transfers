package com.revolut.moneytransfers.config;

import spark.Service;

import javax.inject.Singleton;

@Singleton
public class ConfigurationTest extends Config{

    public ConfigurationTest(int webPort, int threadPool, String dbName) {
        super(webPort, threadPool, Service.ignite().port(webPort).threadPool(threadPool), "sa", "", dbName, 100);
        //Spark.port(webPort);
    }
}
