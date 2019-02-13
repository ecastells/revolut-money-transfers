package com.revolut.moneytransfers.config;

import spark.Service;

import javax.inject.Singleton;

/**
 * The configuration class for test. All configuration for the application an here.
 * dbUser:
 */
@Singleton
public class ConfigurationTest extends Config{

    public ConfigurationTest(int webPort, int threadPool, String dbName) {
        super(webPort, threadPool, Service.ignite().port(webPort).threadPool(threadPool), "sa", "", dbName, 2);
    }
}
