package com.spring.microservices.micro.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class ServiceProxyImpl implements ServiceProxy {

    private MongoClient client;

    private final ObjectMapper mapper = Json.mapper;

    public ServiceProxyImpl(MongoClient client) {
        this.client = client;
    }

    @Override
    public void finAll(String collection, Handler<AsyncResult<String>> handler) {
        client.find(collection, new JsonObject(), result -> {
           if (result.succeeded()) {
               try {
                   handler.handle(Future.succeededFuture(mapper.writeValueAsString(result.result())));
               } catch (JsonProcessingException e) {
                   e.printStackTrace();
                   handler.handle(Future.failedFuture(e.getMessage()));
               }
           } else {
               handler.handle(Future.failedFuture(result.cause()));
           }
        });
    }

    @Override
    public void finById(String collection, String id, Handler<AsyncResult<String>> handler) {

    }

    @Override
    public void create(String collection, JsonObject object, Handler<AsyncResult<String>> handler) {

    }

    @Override
    public void update(String collection, JsonObject object, Handler<AsyncResult<String>> handler) {

    }
}
