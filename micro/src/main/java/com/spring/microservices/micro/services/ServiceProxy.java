package com.spring.microservices.micro.services;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface ServiceProxy {

    void finAll(String collection, Handler<AsyncResult<String>> handler);

    void finById(String collection, String id, Handler<AsyncResult<String>> handler);

    void create(String collection, JsonObject object, Handler<AsyncResult<String>> handler);

    void update(String collection, JsonObject object, Handler<AsyncResult<String>> handler);
}
