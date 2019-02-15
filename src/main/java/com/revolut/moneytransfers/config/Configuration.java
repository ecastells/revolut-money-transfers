package com.revolut.moneytransfers.config;

import spark.Service;

import javax.inject.Singleton;

/**
 * The configuration class. All configurations for prod environment are here.
 */
@Singleton
public class Configuration extends Config{

    public Configuration(int webPort, int threadPool, String dbName) {
        super(webPort, threadPool, Service.ignite().port(webPort).threadPool(threadPool), "sa", "", dbName, 100);
    }
}
