package com.revolut.moneytransfers.controller;

import com.google.gson.Gson;
import com.revolut.moneytransfers.error.ConnectionException;
import com.revolut.moneytransfers.error.ResponseError;
import com.revolut.moneytransfers.error.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ResponseTransformer;
import spark.Spark;

public abstract class GenericController<T extends GenericController> {

    private static final Logger log = LoggerFactory.getLogger(GenericController.class);

    public GenericController() {
        Spark.before((request, response) -> {
            if(log.isTraceEnabled() && request != null)
                log.trace("requestPathInfo: {}, requestBody: {}", request.pathInfo(), request.body());
        });

        Spark.after((request, response) -> {
            response.type("application/json");
            if(log.isTraceEnabled() && response != null)
                log.trace("responseBody: {}, responseStatus: {}", response.body(), response.status());
        });

        Spark.exception(IllegalArgumentException.class, (error, request, response) -> {
            response.status(400);
            response.body(toJson(new ResponseError(error)));
        });

        Spark.exception(ConnectionException.class, (error, request, response) -> {
            response.status(500);
            response.body(toJson(new ResponseError(error)));
        });

        Spark.exception(ValidationException.class, (error, request, response) -> {
            response.status(422);
            response.body(toJson(new ResponseError(error)));
        });

    }

    protected static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    protected ResponseTransformer json() {
        return T::toJson;
    }
}
