package com.spring.microservices.micro.verticles;

import com.spring.microservices.micro.services.ServiceProxyImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class ServicesVerticle extends AbstractVerticle {

    private ServiceProxyImpl serviceProxy;

    private MessageConsumer<JsonObject> consumer;

    @Override
    public void start(Future<Void> future) throws Exception {
        JsonObject config = new JsonObject().put("host", "localhost")
                .put("port", 27017)
                .put("db_name", "servicio");
        serviceProxy = new ServiceProxyImpl(MongoClient.createShared(Vertx.vertx(), config));
        assignConsumer();
        consumer.handler(this::handler);
    }

    protected void assignConsumer() {
        this.consumer = vertx.eventBus().consumer(ServicesVerticle.class.getName());
    }

    protected void handler(Message<JsonObject> message) {
        String action = message.headers().get("action");
        switch (action) {
            case "findAll":
                findAll(message);
                break;
            case "create":
                createT(message);
                break;
        }
    }

    private void findAll(Message<JsonObject> message) {
        JsonObject requestBody = message.body();

        serviceProxy.finAll(requestBody.getString("collection"), stringAsyncResult -> {
            reply(stringAsyncResult, message);
        });
    }

    private void createT(Message<JsonObject> message) {
        JsonObject requestBody = message.body();

        serviceProxy.create(requestBody.getString("collection"), requestBody, stringAsyncResult -> {
            reply(stringAsyncResult, message);
        });
    }

    private void reply(AsyncResult<String> stringAsyncResult, Message<JsonObject> message) {
        if (stringAsyncResult.succeeded()) {
            message.reply(stringAsyncResult.result());
        } else {
            message.reply(stringAsyncResult.cause());
        }
    }

}
